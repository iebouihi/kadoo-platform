package ma.kadoo.platform;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * Sécurité HTTP (NFR-001, NFR-002, NFR-003, KAN-17).
 *
 * <ul>
 *   <li>/api/v1/public/** : lecture publique (catalogue, langues, page cadeau).
 *   <li>/api/v1/webhooks/** : notifications PSP, authentifiées par signature dans le module payment (NFR-005).
 *   <li>/api/v1/auth/login et /csrf : ouverts ; la connexion elle-même est protégée par le jeton CSRF.
 *   <li>/api/v1/admin/** : rôle ADMIN ; /api/v1/partner/** : rôle PARTNER_MANAGER (RG6). Le cloisonnement entre
 *       partenaires se fait ensuite dans chaque module, avec CurrentUser.partnerId() (NFR-003).
 *   <li>Tout le reste : authentifié. Sans session valide : 401, le frontend redirige vers la connexion (AC5).
 * </ul>
 *
 * <p>Session : cookie SESSION HttpOnly, Secure, SameSite=Lax, stockée en base (Spring Session JDBC) ; inactivité
 * 30 min (application.yml), durée maximale 8 h (identity, SessionMaxLifetimeFilter).
 */
@Configuration
class SecurityConfig {

    /** Partagé avec la connexion (identity.AuthController), qui enregistre le contexte dans la session. */
    @Bean
    SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository());
    }

    /**
     * Swagger UI : n'existe qu'en profil local (springdoc.swagger-ui.enabled), 404 ailleurs. Sa page charge ses
     * propres scripts et styles, que la CSP des API (default-src 'none') bloquerait.
     */
    @Bean
    @Order(1)
    SecurityFilterChain swaggerUiSecurity(HttpSecurity http) throws Exception {
        http.securityMatcher("/swagger-ui.html", "/swagger-ui/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .requestCache(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'none'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:;"
                                + " connect-src 'self'; frame-ancestors 'none'")));
        return http.build();
    }

    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http, SecurityContextRepository securityContextRepository)
            throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Rendu d'une erreur déjà décidée (ex. 403 CSRF) : sans cela, il deviendrait un 401.
                        .dispatcherTypeMatchers(DispatcherType.ERROR)
                        .permitAll()
                        .requestMatchers(
                                "/api/v1/public/**",
                                "/api/v1/webhooks/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/csrf",
                                "/actuator/health/**",
                                "/actuator/info",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/v1/partner/**")
                        .hasRole("PARTNER_MANAGER")
                        .anyRequest()
                        .authenticated())
                // Jeton CSRF dans un cookie lisible, renvoyé dans l'en-tête X-XSRF-TOKEN (NFR-002).
                // Les webhooks ne portent pas de jeton CSRF : ils sont protégés par la signature du PSP.
                .csrf(csrf -> csrf.spa().ignoringRequestMatchers("/api/v1/webhooks/**"))
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .exceptionHandling(
                        errors -> errors.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // API sans redirection : pas de mémorisation de la requête refusée (et donc pas de session anonyme).
                .requestCache(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
                .headers(headers -> headers.contentSecurityPolicy(
                        csp -> csp.policyDirectives("default-src 'none'; frame-ancestors 'none'")))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
