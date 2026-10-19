package ma.kadoo.identity;

/** Rôles back-office (KAN-17). Un Admin voit toute la plateforme, un gestionnaire uniquement son partenaire. */
public enum Role {
    ADMIN,
    PARTNER_MANAGER;

    /** Autorité Spring Security correspondante (hasRole). */
    public String authority() {
        return "ROLE_" + name();
    }
}
