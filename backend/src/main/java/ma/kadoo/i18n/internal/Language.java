package ma.kadoo.i18n.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Langue de la plateforme (US-039). Données de référence initialisées par la migration V1. */
@Entity
@Table(name = "language")
public class Language {

    public enum Direction {
        LTR,
        RTL
    }

    @Id
    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "native_name", nullable = false)
    private String nativeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 3)
    private Direction direction;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_default", nullable = false)
    private boolean defaultLanguage;

    protected Language() {}

    public String getCode() {
        return code;
    }

    public String getNativeName() {
        return nativeName;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDefaultLanguage() {
        return defaultLanguage;
    }
}
