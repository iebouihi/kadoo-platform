package ma.kadoo.i18n;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Implémente l'opération listActiveLanguages de contracts/openapi.yaml. */
@RestController
@RequestMapping("/api/v1/public/languages")
class LanguageController {

    private final Languages languages;

    LanguageController(Languages languages) {
        this.languages = languages;
    }

    @GetMapping
    List<LanguageView> listActiveLanguages() {
        return languages.active();
    }
}
