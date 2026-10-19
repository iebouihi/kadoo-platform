package ma.kadoo.identity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** KAN-17 — session : AC5, RG5 (inactivité 30 min, durée max 8 h), RG6 (RBAC) et déconnexion. */
class SessionIT extends BackOfficeIT {

    @Test
    void ac5_apres_30_min_d_inactivite_la_session_est_expiree() throws Exception {
        UUID id = admin("admin@kadoo.test");
        var browser = browser();
        browser.login("admin@kadoo.test", PASSWORD);
        assertThat(browser.get("/api/v1/auth/me").statusCode()).isEqualTo(200);

        vieillirLaSession(id, Duration.ofMinutes(31), Duration.ofMinutes(31));

        assertThat(browser.get("/api/v1/auth/me").statusCode()).isEqualTo(401);
    }

    @Test
    void rg5_une_session_active_depuis_moins_de_30_min_reste_valide() throws Exception {
        UUID id = admin("admin@kadoo.test");
        var browser = browser();
        browser.login("admin@kadoo.test", PASSWORD);

        vieillirLaSession(id, Duration.ofMinutes(29), Duration.ofMinutes(29));

        assertThat(browser.get("/api/v1/auth/me").statusCode()).isEqualTo(200);
    }

    @Test
    void rg5_la_session_expire_apres_8_h_meme_si_elle_reste_active() throws Exception {
        UUID id = admin("admin@kadoo.test");
        var browser = browser();
        browser.login("admin@kadoo.test", PASSWORD);

        vieillirLaSession(id, Duration.ofHours(8).plusMinutes(1), Duration.ZERO);

        assertThat(browser.get("/api/v1/auth/me").statusCode()).isEqualTo(401);
    }

    @Test
    void la_deconnexion_invalide_la_session() throws Exception {
        admin("admin@kadoo.test");
        var browser = browser();
        browser.login("admin@kadoo.test", PASSWORD);

        assertThat(browser.post("/api/v1/auth/logout", "").statusCode()).isEqualTo(204);

        assertThat(browser.get("/api/v1/auth/me").statusCode()).isEqualTo(401);
    }

    @Test
    void sans_session_l_api_back_office_repond_401() throws Exception {
        assertThat(browser().get("/api/v1/auth/me").statusCode()).isEqualTo(401);
        assertThat(browser().get("/api/v1/admin/partners").statusCode()).isEqualTo(401);
    }

    @Test
    void rg6_un_gestionnaire_ne_peut_pas_appeler_l_api_admin() throws Exception {
        manager("gestion@partenaire-a.test", PARTNER_A);
        var browser = browser();
        browser.login("gestion@partenaire-a.test", PASSWORD);

        assertThat(browser.get("/api/v1/admin/partners").statusCode()).isEqualTo(403);
    }

    @Test
    void rg6_un_admin_ne_peut_pas_appeler_l_api_gestionnaire() throws Exception {
        admin("admin@kadoo.test");
        var browser = browser();
        browser.login("admin@kadoo.test", PASSWORD);

        assertThat(browser.get("/api/v1/partner/offers").statusCode()).isEqualTo(403);
    }

    @Test
    void la_connexion_renouvelle_l_identifiant_d_une_session_existante() throws Exception {
        admin("admin@kadoo.test");
        var browser = browser();
        browser.login("admin@kadoo.test", PASSWORD);
        var avant = jdbc.queryForList("select session_id from spring_session", String.class);
        assertThat(avant).hasSize(1);

        browser.login("admin@kadoo.test", PASSWORD);

        assertThat(jdbc.queryForList("select session_id from spring_session", String.class))
                .hasSize(1)
                .doesNotContainAnyElementsOf(avant);
        assertThat(browser.get("/api/v1/auth/me").statusCode()).isEqualTo(200);
    }

    /** Recule dans le temps la création et le dernier accès de la session de l'utilisateur. */
    private void vieillirLaSession(UUID userId, Duration depuisCreation, Duration depuisDernierAcces) {
        jdbc.update(
                """
                update spring_session
                   set creation_time = creation_time - ?,
                       last_access_time = last_access_time - ?,
                       expiry_time = expiry_time - ?
                 where principal_name = ?
                """,
                depuisCreation.toMillis(),
                depuisDernierAcces.toMillis(),
                depuisDernierAcces.toMillis(),
                userId.toString());
    }
}
