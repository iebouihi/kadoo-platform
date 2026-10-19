package ma.kadoo.identity;

import java.sql.Timestamp;
import java.util.UUID;
import ma.kadoo.MutableClockConfiguration;
import ma.kadoo.MutableClockConfiguration.MutableClock;
import ma.kadoo.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Socle des tests back-office (KAN-17) : vraie base, vrai HTTP, horloge pilotable, comptes synthétiques. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, MutableClockConfiguration.class})
abstract class BackOfficeIT {

    /** Mot de passe de test synthétique (10 caractères minimum, RG2). */
    static final String PASSWORD = "Synthetique-2026!";

    static final UUID PARTNER_A = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    static final UUID PARTNER_B = UUID.fromString("00000000-0000-0000-0000-00000000000b");

    @Value("${local.server.port}")
    int port;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MutableClock clock;

    @BeforeEach
    void baseVierge() {
        clock.reset();
        jdbc.update("delete from spring_session");
        jdbc.update("delete from app_user");
    }

    BackOfficeHttp browser() {
        return new BackOfficeHttp(port);
    }

    UUID admin(String email) {
        return user(email, "ADMIN", null, "ACTIVE");
    }

    UUID manager(String email, UUID partnerId) {
        return user(email, "PARTNER_MANAGER", partnerId, "ACTIVE");
    }

    UUID user(String email, String role, UUID partnerId, String status) {
        UUID id = UUID.randomUUID();
        jdbc.update(
                """
                insert into app_user (id, email, password_hash, role, partner_id, status, created_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                email,
                passwordEncoder.encode(PASSWORD),
                role,
                partnerId,
                status,
                Timestamp.from(clock.instant()));
        return id;
    }
}
