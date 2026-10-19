package ma.kadoo.identity.internal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * RG5 : une session back-office dure 8 h au plus, même active. L'inactivité de 30 min est gérée par Spring Session
 * (server.servlet.session.timeout). Placé après le filtre Spring Session et avant Spring Security : une session
 * invalidée ici rend la requête anonyme, donc refusée (401).
 */
class SessionMaxLifetimeFilter extends OncePerRequestFilter {

    static final Duration MAX_LIFETIME = Duration.ofHours(8);

    private final Clock clock;

    SessionMaxLifetimeFilter(Clock clock) {
        this.clock = clock;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null
                && Instant.ofEpochMilli(session.getCreationTime())
                        .plus(MAX_LIFETIME)
                        .isBefore(clock.instant())) {
            session.invalidate();
        }
        chain.doFilter(request, response);
    }
}
