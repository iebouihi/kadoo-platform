package ma.kadoo.identity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;
import ma.kadoo.platform.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * KAN-17 AC4 / NFR-003 — un gestionnaire du partenaire A n'obtient jamais une ressource de B : 404, sans aucune
 * donnée de B. Aucune ressource partenaire n'existe encore : le mécanisme est prouvé sur un endpoint de test qui
 * suit la règle imposée aux futurs endpoints (partnerId issu de la session, jamais de la requête).
 */
@Import(CrossTenantIT.Fixture.class)
class CrossTenantIT extends BackOfficeIT {

    static final UUID RESSOURCE_DE_A = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
    static final UUID RESSOURCE_DE_B = UUID.fromString("00000000-0000-0000-0000-0000000000b1");

    @Test
    void ac4_un_gestionnaire_de_A_obtient_404_sans_donnee_sur_une_ressource_de_B() throws Exception {
        manager("gestion@partenaire-a.test", PARTNER_A);
        var browser = browser();
        browser.login("gestion@partenaire-a.test", PASSWORD);

        var sienne = browser.get("/api/v1/partner/test-resources/" + RESSOURCE_DE_A);
        var autre = browser.get("/api/v1/partner/test-resources/" + RESSOURCE_DE_B);
        var inexistante = browser.get("/api/v1/partner/test-resources/" + UUID.randomUUID());

        assertThat(sienne.statusCode()).isEqualTo(200);
        assertThat(sienne.body()).contains("Boutique A");
        assertThat(autre.statusCode()).isEqualTo(404);
        // L'identifiant demandé vient de l'appelant ; seules les données de B ne doivent pas apparaître.
        assertThat(autre.body()).doesNotContain("Boutique B", PARTNER_B.toString());
        assertThat(autre.body()).contains("\"code\":\"RESOURCE_NOT_FOUND\"");
        assertThat(inexistante.statusCode()).isEqualTo(404);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class Fixture {

        @Bean
        TestResourceController testResourceController(CurrentUser currentUser) {
            return new TestResourceController(currentUser);
        }
    }

    @RestController
    static class TestResourceController {

        record TestResource(UUID partnerId, String name) {}

        private static final Map<UUID, TestResource> RESOURCES = Map.of(
                RESSOURCE_DE_A, new TestResource(PARTNER_A, "Boutique A"),
                RESSOURCE_DE_B, new TestResource(PARTNER_B, "Boutique B"));

        private final CurrentUser currentUser;

        TestResourceController(CurrentUser currentUser) {
            this.currentUser = currentUser;
        }

        @GetMapping("/api/v1/partner/test-resources/{id}")
        TestResource get(@PathVariable UUID id) {
            UUID partnerId = currentUser.partnerId();
            TestResource resource = RESOURCES.get(id);
            if (resource == null || !resource.partnerId().equals(partnerId)) {
                throw new NotFoundException();
            }
            return resource;
        }
    }
}
