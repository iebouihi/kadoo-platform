package ma.kadoo.identity.internal;

import java.time.Clock;
import java.util.UUID;
import ma.kadoo.audit.AuditLog;
import ma.kadoo.identity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Amorçage du premier Admin (hypothèse KAN-17, à confirmer par le PO) : aucune story ne crée le premier compte,
 * et les invitations (KAN-18) exigent un Admin. Si aucun Admin n'existe et que KADOO_BOOTSTRAP_ADMIN_EMAIL et
 * KADOO_BOOTSTRAP_ADMIN_PASSWORD sont définies, le compte est créé actif et journalisé. Sans effet ensuite.
 * Aucun identifiant n'est stocké dans le dépôt ni dans les migrations (NFR-004).
 */
@Component
class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuditLog auditLog;
    private final Clock clock;
    private final String email;
    private final String password;

    AdminBootstrap(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            AuditLog auditLog,
            Clock clock,
            @Value("${kadoo.bootstrap.admin.email:}") String email,
            @Value("${kadoo.bootstrap.admin.password:}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.auditLog = auditLog;
        this.clock = clock;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank() || users.existsByRole(Role.ADMIN)) {
            return;
        }
        User.checkPasswordPolicy(password);
        User admin = users.save(
                User.bootstrapAdmin(UUID.randomUUID(), email, passwordEncoder.encode(password), clock.instant()));
        auditLog.record(new AuditLog.Entry(
                "User",
                admin.getId().toString(),
                "ADMIN_BOOTSTRAPPED",
                AuditLog.ActorType.SYSTEM,
                null,
                null,
                "{\"role\":\"ADMIN\",\"status\":\"ACTIVE\"}"));
        log.info("Premier compte Admin créé depuis la configuration (id={})", admin.getId());
    }
}
