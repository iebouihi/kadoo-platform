package ma.kadoo.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.dao.DuplicateKeyException;

/** KAN-17 — connexion : AC1, AC2, AC3 et règles de gestion RG1 à RG4. */
@ExtendWith(OutputCaptureExtension.class)
class LoginIT extends BackOfficeIT {

    @Test
    void ac1_un_compte_actif_se_connecte_avec_son_role_et_lastLoginAt_est_mis_a_jour() throws Exception {
        UUID id = admin("admin@kadoo.test");
        var browser = browser();

        var response = browser.login("admin@kadoo.test", PASSWORD);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"role\":\"ADMIN\"");
        assertThat(browser.hasSession()).isTrue();
        assertThat(jdbc.queryForObject("select last_login_at from app_user where id = ?", Timestamp.class, id))
                .isEqualTo(Timestamp.from(clock.instant()));

        var me = browser.get("/api/v1/auth/me");
        assertThat(me.statusCode()).isEqualTo(200);
        assertThat(me.body()).contains("\"email\":\"admin@kadoo.test\"", "\"role\":\"ADMIN\"");
    }

    @Test
    void ac1_un_gestionnaire_recoit_son_role_et_son_partenaire() throws Exception {
        manager("gestion@partenaire-a.test", PARTNER_A);
        var browser = browser();

        assertThat(browser.login("gestion@partenaire-a.test", PASSWORD).body())
                .contains("\"role\":\"PARTNER_MANAGER\"");
        assertThat(browser.get("/api/v1/auth/me").body()).contains("\"partnerId\":\"" + PARTNER_A + "\"");
    }

    @Test
    void ac2_identifiants_invalides_message_generique_sans_reveler_si_l_email_existe() throws Exception {
        admin("admin@kadoo.test");

        var mauvaisMotDePasse = browser().login("admin@kadoo.test", "pas-le-bon-mot-de-passe");
        var emailInconnu = browser().login("inconnu@kadoo.test", "pas-le-bon-mot-de-passe");

        assertThat(mauvaisMotDePasse.statusCode()).isEqualTo(401);
        assertThat(mauvaisMotDePasse.body()).contains("\"code\":\"INVALID_CREDENTIALS\"");
        assertThat(emailInconnu.statusCode()).isEqualTo(mauvaisMotDePasse.statusCode());
        assertThat(emailInconnu.body()).isEqualTo(mauvaisMotDePasse.body());
    }

    @Test
    void ac3_cinq_echecs_consecutifs_verrouillent_le_compte_15_min_et_creent_LOGIN_LOCKED() throws Exception {
        UUID id = admin("admin@kadoo.test");
        for (int i = 0; i < 5; i++) {
            assertThat(browser()
                            .login("admin@kadoo.test", "mauvais-mot-de-passe")
                            .statusCode())
                    .isEqualTo(401);
        }

        var pendantLeVerrou = browser().login("admin@kadoo.test", PASSWORD);
        assertThat(pendantLeVerrou.statusCode()).isEqualTo(401);
        assertThat(pendantLeVerrou.body()).contains("\"code\":\"INVALID_CREDENTIALS\"");
        assertThat(jdbc.queryForObject(
                        "select count(*) from audit_event where entity_type = 'User' and entity_id = ?"
                                + " and event_type = 'LOGIN_LOCKED'",
                        Long.class,
                        id.toString()))
                .isEqualTo(1L);

        clock.advance(Duration.ofMinutes(14));
        assertThat(browser().login("admin@kadoo.test", PASSWORD).statusCode()).isEqualTo(401);

        clock.advance(Duration.ofMinutes(1));
        assertThat(browser().login("admin@kadoo.test", PASSWORD).statusCode()).isEqualTo(200);
    }

    @Test
    void rg4_une_connexion_reussie_remet_le_compteur_d_echecs_a_zero() throws Exception {
        UUID id = admin("admin@kadoo.test");
        for (int i = 0; i < 4; i++) {
            browser().login("admin@kadoo.test", "mauvais-mot-de-passe");
        }
        assertThat(browser().login("admin@kadoo.test", PASSWORD).statusCode()).isEqualTo(200);

        browser().login("admin@kadoo.test", "mauvais-mot-de-passe");

        assertThat(browser().login("admin@kadoo.test", PASSWORD).statusCode()).isEqualTo(200);
        assertThat(jdbc.queryForObject("select failed_login_count from app_user where id = ?", Integer.class, id))
                .isZero();
    }

    @Test
    void rg1_l_email_est_compare_sans_tenir_compte_de_la_casse() throws Exception {
        admin("admin@kadoo.test");

        assertThat(browser().login("Admin@KADOO.test", PASSWORD).statusCode()).isEqualTo(200);
    }

    @Test
    void rg1_l_email_est_unique_sans_tenir_compte_de_la_casse() {
        admin("admin@kadoo.test");

        assertThatThrownBy(() -> admin("ADMIN@kadoo.test")).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void rg2_le_mot_de_passe_est_hashe_en_argon2id_et_jamais_journalise(CapturedOutput output) throws Exception {
        UUID id = admin("admin@kadoo.test");
        String essai = "Essai-Journalisation-42";
        for (int i = 0; i < 5; i++) {
            browser().login("admin@kadoo.test", essai);
        }
        browser().login("admin@kadoo.test", PASSWORD);

        assertThat(jdbc.queryForObject("select password_hash from app_user where id = ?", String.class, id))
                .startsWith("$argon2id$");
        assertThat(output.getAll()).doesNotContain(essai, PASSWORD);
        assertThat(jdbc.queryForList("select coalesce(changes, '') from audit_event", String.class))
                .noneMatch(changes -> changes.contains(essai) || changes.contains(PASSWORD));
    }

    @Test
    void rg3_seuls_les_comptes_actifs_peuvent_se_connecter() throws Exception {
        user("invite@kadoo.test", "ADMIN", null, "INVITED");
        user("desactive@partenaire-a.test", "PARTNER_MANAGER", PARTNER_A, "DISABLED");
        var reference = browser().login("inconnu@kadoo.test", PASSWORD);

        var invite = browser().login("invite@kadoo.test", PASSWORD);
        var desactive = browser().login("desactive@partenaire-a.test", PASSWORD);

        assertThat(invite.statusCode()).isEqualTo(401);
        assertThat(invite.body()).isEqualTo(reference.body());
        assertThat(desactive.statusCode()).isEqualTo(401);
        assertThat(desactive.body()).isEqualTo(reference.body());
    }

    @Test
    void la_connexion_exige_le_jeton_csrf() throws Exception {
        admin("admin@kadoo.test");

        var response = browser()
                .postWithoutCsrf(
                        "/api/v1/auth/login",
                        "{\"email\":\"admin@kadoo.test\",\"password\":\"%s\"}".formatted(PASSWORD),
                        null);

        assertThat(response.statusCode()).isEqualTo(403);
    }
}
