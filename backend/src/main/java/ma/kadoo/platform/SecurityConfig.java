package ma.kadoo.platform;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Sécurité HTTP (NFR-001, NFR-002, NFR-003).
 *
 * <ul>
 *   <li>/api/v1/public/** : lecture publique (catalogue, langues, page cadeau).
 *   <li>/api/v1/webhooks/** : notifications PSP, authentifiées par signature dans le module payment (NFR-005).
 *   <li>Tout le reste : authentifié. La connexion back-office (US-001) remplacera l'authentification par défaut.
 * </ul>
 */
@Configuration
class SecurityConfig {

    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/api/v1/public/**",
                                "/api/v1/webhooks/**",
                                "/actuator/health/**",
                                "/actuator/info",
                                "/v3/api-docs/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                // Les webhooks ne portent pas de jeton CSRF : ils sont protégés par la signature du PSP.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/webhooks/**"))
                .headers(headers -> headers.contentSecurityPolicy(
                        csp -> csp.policyDirectives("default-src 'none'; frame-ancestors 'none'")))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
