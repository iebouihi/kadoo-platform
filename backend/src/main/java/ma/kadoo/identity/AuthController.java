package ma.kadoo.identity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import ma.kadoo.identity.internal.BackOfficeSession;
import ma.kadoo.identity.internal.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implémente getCsrfToken, login et getCurrentUser de contracts/openapi.yaml (KAN-17). La déconnexion est
 * assurée par Spring Security (POST /api/v1/auth/logout, voir SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    record LoginRequest(@NotBlank @Size(max = 254) String email, @NotBlank @Size(max = 200) String password) {

        /** Le mot de passe ne doit jamais apparaître dans un log, même via toString (RG2). */
        @Override
        public String toString() {
            return "LoginRequest[email=" + email + ", password=***]";
        }
    }

    record LoginResult(Role role) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record CurrentUserView(String email, Role role, UUID partnerId) {}

    private final LoginService loginService;
    private final CurrentUser currentUser;
    private final BackOfficeSession backOfficeSession;

    AuthController(LoginService loginService, CurrentUser currentUser, BackOfficeSession backOfficeSession) {
        this.loginService = loginService;
        this.currentUser = currentUser;
        this.backOfficeSession = backOfficeSession;
    }

    /** Le gestionnaire CSRF « SPA » dépose le cookie XSRF-TOKEN sur chaque réponse ; cet appel suffit à l'obtenir. */
    @GetMapping("/csrf")
    ResponseEntity<Void> csrf(CsrfToken token) {
        token.getToken();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest body, HttpServletRequest request, HttpServletResponse response) {
        var user = loginService.login(body.email(), body.password(), request.getRemoteAddr());
        if (user.isEmpty()) {
            ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
            problem.setProperty("code", "INVALID_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
        }
        SessionUser sessionUser = user.get();
        backOfficeSession.open(sessionUser, request, response);
        return ResponseEntity.ok(new LoginResult(sessionUser.role()));
    }

    @GetMapping("/me")
    CurrentUserView me() {
        SessionUser user = currentUser.get();
        return new CurrentUserView(user.email(), user.role(), user.partnerId());
    }
}
