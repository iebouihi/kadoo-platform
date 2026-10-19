package ma.kadoo.identity.internal;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import ma.kadoo.audit.AuditLog;
import ma.kadoo.identity.SessionUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Vérification des identifiants back-office (KAN-17). Tout refus est indiscernable de l'extérieur (AC2) : même
 * réponse et même coût de calcul, que l'email existe ou non, que le compte soit actif, verrouillé ou non.
 */
@Service
public class LoginService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuditLog auditLog;
    private final Clock clock;

    /** Hash d'un mot de passe aléatoire : sert à dépenser le même temps quand aucun hash réel n'est comparé. */
    private final String dummyHash;

    LoginService(UserRepository users, PasswordEncoder passwordEncoder, AuditLog auditLog, Clock clock) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.auditLog = auditLog;
        this.clock = clock;
        this.dummyHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    /** Renvoie l'utilisateur à placer en session, ou vide si la connexion est refusée, quel qu'en soit le motif. */
    @Transactional
    public Optional<SessionUser> login(String email, String password, String ip) {
        Instant now = clock.instant();
        Optional<User> found = users.findForLogin(User.normalizeEmail(email));
        if (found.isEmpty() || !found.get().canAttemptLogin(now)) {
            passwordEncoder.matches(password, dummyHash);
            return Optional.empty();
        }
        User user = found.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            if (user.recordFailedLogin(now)) {
                auditLog.record(new AuditLog.Entry(
                        "User",
                        user.getId().toString(),
                        "LOGIN_LOCKED",
                        AuditLog.ActorType.SYSTEM,
                        null,
                        user.getPartnerId(),
                        "{\"lockedUntil\":\"" + user.getLockedUntil() + "\"}",
                        ip));
            }
            return Optional.empty();
        }
        user.recordSuccessfulLogin(now);
        return Optional.of(new SessionUser(user.getId(), user.getEmail(), user.getRole(), user.getPartnerId()));
    }
}
