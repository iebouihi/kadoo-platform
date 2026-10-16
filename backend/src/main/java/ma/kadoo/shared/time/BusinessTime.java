package ma.kadoo.shared.time;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Temps métier (NFR-015) : stockage en UTC, calculs et affichage dans le fuseau du Maroc.
 * Le Maroc change d'heure pendant le Ramadan : ne jamais coder un décalage fixe, toujours passer par la base tz.
 */
public final class BusinessTime {

    public static final ZoneId ZONE = ZoneId.of("Africa/Casablanca");
    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59, 59);

    private BusinessTime() {}

    /**
     * Expiration d'une carte (US-022) : 23:59:59 heure du Maroc, le jour (émission + validityDays).
     */
    public static Instant expiryOf(Instant issuedAt, int validityDays) {
        if (validityDays <= 0) {
            throw new IllegalArgumentException("validityDays doit être positif : " + validityDays);
        }
        LocalDate issuedDay = issuedAt.atZone(ZONE).toLocalDate();
        return issuedDay.plusDays(validityDays).atTime(END_OF_DAY).atZone(ZONE).toInstant();
    }

    public static LocalDate today(Clock clock) {
        return LocalDate.now(clock.withZone(ZONE));
    }
}
