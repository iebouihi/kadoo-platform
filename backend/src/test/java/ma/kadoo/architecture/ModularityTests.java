package ma.kadoo.architecture;

import ma.kadoo.KadooApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/** NFR-040 : aucune dépendance interdite entre modules, aucun accès aux sous-packages internes d'un autre module. */
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(KadooApplication.class);

    @Test
    void les_frontieres_de_modules_sont_respectees() {
        modules.verify();
    }
}
