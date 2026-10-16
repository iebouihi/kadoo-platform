package ma.kadoo.shared.money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Critères d'acceptation de US-019 et règles de US-005. */
class CommissionCalculatorTest {

    @ParameterizedTest(name = "{0} MAD à {1} % → commission {2}, partenaire {3}")
    @CsvSource({
        "500.00, 7.50, 37.50, 462.50", // US-019 AC1
        "199.99, 3.33, 6.66, 193.33", // US-019 AC2 (6.659667 arrondi HALF_UP)
        "100.00, 3.00, 3.00, 97.00", // borne basse
        "100.00, 10.00, 10.00, 90.00", // borne haute
        "0.10, 5.00, 0.01, 0.09" // 0.005 arrondi au centime supérieur
    })
    void calcule_commission_et_part_partenaire(String base, String rate, String commission, String partner) {
        var split = CommissionCalculator.split(Money.mad(base), new BigDecimal(rate));

        assertThat(split.commission()).isEqualTo(Money.mad(commission));
        assertThat(split.partnerAmount()).isEqualTo(Money.mad(partner));
        assertThat(split.commission().plus(split.partnerAmount())).isEqualTo(split.base());
    }

    @ParameterizedTest
    @CsvSource({"2.99", "10.01", "2.50", "10.50"})
    void refuse_un_taux_hors_bornes(String rate) {
        assertThatThrownBy(() -> CommissionCalculator.split(Money.mad("100.00"), new BigDecimal(rate)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuse_un_taux_a_plus_de_deux_decimales() {
        assertThatThrownBy(() -> CommissionCalculator.requireValidRate(new BigDecimal("5.125")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuse_une_base_nulle_ou_negative() {
        assertThatThrownBy(() -> CommissionCalculator.split(Money.mad("0.00"), new BigDecimal("5")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void money_refuse_une_autre_devise() {
        assertThatThrownBy(() -> new Money(new BigDecimal("10"), "EUR")).isInstanceOf(IllegalArgumentException.class);
    }
}
