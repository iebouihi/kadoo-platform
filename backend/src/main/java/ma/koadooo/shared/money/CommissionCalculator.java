package ma.koadooo.shared.money;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Calcul de la commission KOADOOO et de la part partenaire (US-019).
 *
 * <ul>
 *   <li>Base = montant payé TTC (hypothèse D-02).
 *   <li>Commission = base × taux figé à la création de la commande, arrondie au centime (HALF_UP).
 *   <li>Part partenaire = base − commission.
 * </ul>
 */
public final class CommissionCalculator {

    public static final BigDecimal MIN_RATE_PERCENT = new BigDecimal("3.00");
    public static final BigDecimal MAX_RATE_PERCENT = new BigDecimal("10.00");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private CommissionCalculator() {}

    public static Split split(Money base, BigDecimal ratePercent) {
        Objects.requireNonNull(base, "base");
        requireValidRate(ratePercent);
        if (!base.isPositive()) {
            throw new IllegalArgumentException("La base de commission doit être positive : " + base.toPlainString());
        }
        Money commission = Money.mad(base.amount().multiply(ratePercent).divide(HUNDRED, Money.SCALE, Money.ROUNDING));
        return new Split(base, ratePercent, commission, base.minus(commission));
    }

    /** Taux contractuel entre 3,00 % et 10,00 %, 2 décimales maximum (US-005). */
    public static void requireValidRate(BigDecimal ratePercent) {
        Objects.requireNonNull(ratePercent, "ratePercent");
        if (ratePercent.compareTo(MIN_RATE_PERCENT) < 0 || ratePercent.compareTo(MAX_RATE_PERCENT) > 0) {
            throw new IllegalArgumentException("Taux hors bornes [3,00 % ; 10,00 %] : " + ratePercent);
        }
        if (ratePercent.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Taux limité à 2 décimales : " + ratePercent);
        }
    }

    public record Split(Money base, BigDecimal ratePercent, Money commission, Money partnerAmount) {}
}
