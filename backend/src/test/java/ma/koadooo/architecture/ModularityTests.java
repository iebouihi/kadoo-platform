package ma.koadooo.architecture;

import ma.koadooo.KoadoooApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/** NFR-040 : aucune dépendance interdite entre modules, aucun accès aux sous-packages internes d'un autre module. */
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(KoadoooApplication.class);

    @Test
    void les_frontieres_de_modules_sont_respectees() {
        modules.verify();
    }
}
