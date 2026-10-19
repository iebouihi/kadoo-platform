package ma.kadoo.identity.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** KAN-17 RG1, RG2, RG4 : règles portées par le compte, sans base ni horloge système. */
class UserTest {

    private static final Instant T0 = Instant.parse("2026-10-19T09:00:00Z");

    private static User actif() {
        return User.bootstrapAdmin(UUID.randomUUID(), "admin@kadoo.test", "hash", T0);
    }

    @Test
    void rg4_le_cinquieme_echec_consecutif_verrouille_pour_15_minutes() {
        User user = actif();
        for (int i = 0; i < 4; i++) {
            assertThat(user.recordFailedLogin(T0)).isFalse();
        }

        assertThat(user.recordFailedLogin(T0)).isTrue();

        assertThat(user.isLocked(T0.plus(Duration.ofMinutes(14)).plusSeconds(59)))
                .isTrue();
        assertThat(user.isLocked(T0.plus(Duration.ofMinutes(15)))).isFalse();
    }

    @Test
    void rg4_une_connexion_reussie_remet_le_compteur_a_zero() {
        User user = actif();
        for (int i = 0; i < 4; i++) {
            user.recordFailedLogin(T0);
        }

        user.recordSuccessfulLogin(T0);

        for (int i = 0; i < 4; i++) {
            assertThat(user.recordFailedLogin(T0)).isFalse();
        }
        assertThat(user.getLastLoginAt()).isEqualTo(T0);
    }

    @Test
    void rg1_l_email_est_normalise_en_minuscules() {
        assertThat(User.normalizeEmail("  Admin@KADOO.test ")).isEqualTo("admin@kadoo.test");
    }

    @Test
    void rg2_un_mot_de_passe_de_moins_de_10_caracteres_est_refuse() {
        assertThatThrownBy(() -> User.checkPasswordPolicy("Court-123")).isInstanceOf(IllegalArgumentException.class);
        User.checkPasswordPolicy("Assez-long1");
    }
}
