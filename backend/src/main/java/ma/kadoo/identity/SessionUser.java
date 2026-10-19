package ma.kadoo.identity;

import java.io.Serializable;
import java.security.Principal;
import java.util.UUID;

/**
 * Utilisateur authentifié, conservé dans la session (Spring Session JDBC, sérialisé en base). Aucun secret :
 * ni mot de passe ni hash. Le nom du principal est l'identifiant, jamais l'email.
 */
public record SessionUser(UUID id, String email, Role role, UUID partnerId) implements Principal, Serializable {

    @Override
    public String getName() {
        return id.toString();
    }
}
