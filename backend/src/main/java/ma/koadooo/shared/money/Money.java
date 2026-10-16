package ma.koadooo.shared.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Montant exact en dirhams (NFR-014). Toujours 2 décimales, arrondi au centime demi-supérieur.
 * Aucun double / float n'est accepté : la règle ArchUnit {@code ArchitectureRulesTest} le vérifie.
 */
public record Money(BigDecimal amount, String currency) implements Comparable<Money> {

    public static final String MAD = "MAD";
    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (!MAD.equals(currency)) {
            throw new IllegalArgumentException("Seule la devise MAD est acceptée au MVP : " + currency);
        }
        amount = amount.setScale(SCALE, ROUNDING);
    }

    public static Money mad(String amount) {
        return new Money(new BigDecimal(amount), MAD);
    }

    public static Money mad(BigDecimal amount) {
        return new Money(amount, MAD);
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    /** Forme transmise dans l'API : chaîne décimale "1250.00" (contracts/openapi.yaml#Money). */
    public String toPlainString() {
        return amount.toPlainString();
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Devises différentes : " + currency + " / " + other.currency);
        }
    }
}
