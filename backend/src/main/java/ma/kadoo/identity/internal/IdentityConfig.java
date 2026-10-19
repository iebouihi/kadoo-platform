package ma.kadoo.identity.internal;

import java.time.Clock;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class IdentityConfig {

    /** RG2 / NFR-002 : Argon2id, paramètres minimaux recommandés par l'OWASP (19 Mio, 2 itérations). */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 19 * 1024, 2);
    }

    @Bean
    FilterRegistrationBean<SessionMaxLifetimeFilter> sessionMaxLifetimeFilter(Clock clock) {
        var registration = new FilterRegistrationBean<>(new SessionMaxLifetimeFilter(clock));
        registration.setOrder(SecurityFilterProperties.DEFAULT_FILTER_ORDER - 1);
        return registration;
    }
}
