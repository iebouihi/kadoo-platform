package ma.koadooo.shared.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

/** Règle d'expiration de US-022 / US-029 et NFR-015. */
class BusinessTimeTest {

    @Test
    void expire_a_23h59m59_heure_du_maroc() {
        Instant issuedAt = Instant.parse("2026-10-16T10:00:00Z");

        ZonedDateTime expiry = BusinessTime.expiryOf(issuedAt, 365).atZone(BusinessTime.ZONE);

        assertThat(expiry.toLocalDate()).hasToString("2027-10-16");
        assertThat(expiry.toLocalTime()).hasToString("23:59:59");
    }

    @Test
    void le_jour_d_emission_est_celui_du_maroc_et_non_celui_d_utc() {
        // 23:30 UTC le 31/12 correspond déjà au 01/01 à Casablanca (UTC+1 hors Ramadan)
        Instant issuedAt = Instant.parse("2026-12-31T23:30:00Z");

        ZonedDateTime expiry = BusinessTime.expiryOf(issuedAt, 30).atZone(BusinessTime.ZONE);

        assertThat(expiry.toLocalDate()).hasToString("2027-01-31");
    }

    @Test
    void refuse_une_validite_nulle() {
        assertThatThrownBy(() -> BusinessTime.expiryOf(Instant.now(), 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
