package ma.kadoo;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** Horloge pilotable par les tests (NFR-015) : remplace le bean Clock de production. */
@TestConfiguration(proxyBeanMethods = false)
public class MutableClockConfiguration {

    @Bean
    @Primary
    MutableClock mutableClock() {
        return new MutableClock();
    }

    public static class MutableClock extends Clock {

        private volatile Instant now = realNow();

        /** Précision de PostgreSQL (microseconde), pour comparer un instant relu en base. */
        private static Instant realNow() {
            return Clock.systemUTC().instant().truncatedTo(ChronoUnit.MICROS);
        }

        public void reset() {
            now = realNow();
        }

        public void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public Instant instant() {
            return now;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.fixed(now, zone);
        }
    }
}
