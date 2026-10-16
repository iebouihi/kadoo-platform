package ma.koadooo.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Règles d'architecture exécutées à chaque build. Elles traduisent des NFR en contrôles automatiques,
 * y compris pour le code produit avec un assistant IA.
 */
@AnalyzeClasses(packages = "ma.koadooo", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureRulesTest {

    /** NFR-014 : aucun montant ni taux en virgule flottante. */
    @ArchTest
    static final ArchRule pas_de_float_ni_double = fields().should()
            .notHaveRawType(double.class)
            .andShould()
            .notHaveRawType(float.class)
            .andShould()
            .notHaveRawType(Double.class)
            .andShould()
            .notHaveRawType(Float.class)
            .because("les montants et taux utilisent BigDecimal / Money (NFR-014)");

    /** NFR-015 : le temps passe par l'horloge injectée. */
    @ArchTest
    static final ArchRule pas_d_horloge_systeme_directe = noClasses()
            .should()
            .callMethod(Instant.class, "now")
            .orShould()
            .callMethod(LocalDate.class, "now")
            .orShould()
            .callMethod(LocalDateTime.class, "now")
            .orShould()
            .callMethod(Clock.class, "systemDefaultZone")
            .because("le code métier utilise le bean Clock pour être testable (NFR-015)");

    /** NFR-015 : java.util.Date et Calendar sont interdits. */
    @ArchTest
    static final ArchRule pas_de_java_util_date = noClasses()
            .should()
            .dependOnClassesThat()
            .haveFullyQualifiedName("java.util.Date")
            .orShould()
            .dependOnClassesThat()
            .haveFullyQualifiedName("java.util.Calendar")
            .because("utiliser java.time (NFR-015)");

    /** Les contrôleurs ne parlent pas directement aux dépôts : ils passent par l'API du module. */
    @ArchTest
    static final ArchRule controleurs_sans_acces_aux_depots = noClasses()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .dependOnClassesThat()
            .haveSimpleNameEndingWith("Repository");

    /** NFR-004 / NFR-060 : pas de sortie console ni de printStackTrace. */
    @ArchTest
    static final ArchRule pas_de_system_out = noClasses()
            .should()
            .accessField(System.class, "out")
            .orShould()
            .accessField(System.class, "err")
            .because("utiliser le logger structuré (NFR-060)");
}
