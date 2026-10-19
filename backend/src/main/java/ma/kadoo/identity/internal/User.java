package ma.kadoo.identity.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import ma.kadoo.identity.Role;

/** Compte back-office (KAN-17). Cycle de vie : INVITED → ACTIVE ⇄ DISABLED (onglet 04). */
@Entity
@Table(name = "app_user")
public class User {

    public enum Status {
        INVITED,
        ACTIVE,
        DISABLED
    }

    /** RG4 : verrouillage après 5 échecs consécutifs, pour 15 minutes. */
    static final int MAX_FAILED_LOGINS = 5;

    static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    /** RG2 : longueur minimale d'un mot de passe. */
    static final int MIN_PASSWORD_LENGTH = 10;

    @Id
    private UUID id;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "partner_id")
    private UUID partnerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected User() {}

    /** Premier Admin, créé actif depuis la configuration (amorçage, voir AdminBootstrap). */
    static User bootstrapAdmin(UUID id, String email, String passwordHash, Instant now) {
        User user = new User();
        user.id = id;
        user.email = normalizeEmail(email);
        user.passwordHash = passwordHash;
        user.role = Role.ADMIN;
        user.status = Status.ACTIVE;
        user.createdAt = now;
        return user;
    }

    /** RG1 : l'email est comparé sans tenir compte de la casse ; il est stocké normalisé. */
    static String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    /** RG2 : refuse un mot de passe trop court. Le message ne contient jamais le mot de passe. */
    static void checkPasswordPolicy(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères");
        }
    }

    boolean isLocked(Instant now) {
        return lockedUntil != null && now.isBefore(lockedUntil);
    }

    /** RG3 et RG4 : seuls les comptes actifs et non verrouillés peuvent tenter une connexion. */
    boolean canAttemptLogin(Instant now) {
        return status == Status.ACTIVE && passwordHash != null && !isLocked(now);
    }

    /**
     * Enregistre un échec. Renvoie {@code true} si cet échec verrouille le compte (le compteur repart alors de
     * zéro pour la période suivante).
     */
    boolean recordFailedLogin(Instant now) {
        failedLoginCount++;
        if (failedLoginCount < MAX_FAILED_LOGINS) {
            return false;
        }
        failedLoginCount = 0;
        lockedUntil = now.plus(LOCK_DURATION);
        return true;
    }

    void recordSuccessfulLogin(Instant now) {
        failedLoginCount = 0;
        lockedUntil = null;
        lastLoginAt = now;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public UUID getPartnerId() {
        return partnerId;
    }

    Instant getLockedUntil() {
        return lockedUntil;
    }

    Instant getLastLoginAt() {
        return lastLoginAt;
    }
}
