package ma.kadoo.identity;

import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * API du module identity pour les autres modules : qui appelle, avec quel rôle, pour quel partenaire.
 *
 * <p>NFR-003 : le partnerId d'une requête back-office vient toujours d'ici (la session), jamais d'un paramètre,
 * d'un corps de requête ou d'une URL. Une ressource d'un autre partenaire se traite comme absente (404).
 */
@Component
public class CurrentUser {

    public SessionUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SessionUser user)) {
            throw new AuthenticationCredentialsNotFoundException("Aucun utilisateur back-office authentifié");
        }
        return user;
    }

    /** Partenaire du gestionnaire connecté ; refusé pour un Admin, qui n'appartient à aucun partenaire. */
    public UUID partnerId() {
        SessionUser user = get();
        if (user.role() != Role.PARTNER_MANAGER) {
            throw new AccessDeniedException("Réservé aux gestionnaires partenaires");
        }
        return user.partnerId();
    }
}
