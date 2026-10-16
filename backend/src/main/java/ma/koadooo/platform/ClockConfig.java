package ma.koadooo.platform;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Horloge injectable (NFR-015) : aucun code métier n'appelle Instant.now() directement,
 * pour pouvoir tester l'expiration et les changements d'heure.
 */
@Configuration
class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
