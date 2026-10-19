package ma.kadoo.identity.internal;

import static org.assertj.core.api.Assertions.assertThat;

import ma.kadoo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Amorçage du premier Admin (hypothèse KAN-17, à confirmer par le PO) : créé au démarrage depuis la configuration
 * si aucun Admin n'existe, une seule fois, et journalisé.
 */
@SpringBootTest(
        properties = {
            "kadoo.bootstrap.admin.email=Premier.Admin@kadoo.test",
            "kadoo.bootstrap.admin.password=Synthetique-2026!"
        })
@Import(TestcontainersConfiguration.class)
class AdminBootstrapIT {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    AdminBootstrap bootstrap;

    @Test
    void cree_le_premier_admin_actif_une_seule_fois_et_le_journalise() throws Exception {
        bootstrap.run(new DefaultApplicationArguments());

        var admins = jdbc.queryForList("select id, email, role, status, partner_id, password_hash from app_user");
        assertThat(admins).hasSize(1);
        var admin = admins.getFirst();
        assertThat(admin.get("email")).isEqualTo("premier.admin@kadoo.test");
        assertThat(admin.get("role")).isEqualTo("ADMIN");
        assertThat(admin.get("status")).isEqualTo("ACTIVE");
        assertThat(admin.get("partner_id")).isNull();
        assertThat((String) admin.get("password_hash")).startsWith("$argon2id$");
        assertThat(jdbc.queryForObject(
                        "select count(*) from audit_event where entity_type = 'User' and entity_id = ?"
                                + " and event_type = 'ADMIN_BOOTSTRAPPED' and actor_type = 'SYSTEM'",
                        Long.class,
                        admin.get("id").toString()))
                .isEqualTo(1L);
    }
}
