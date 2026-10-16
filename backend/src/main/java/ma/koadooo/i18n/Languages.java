package ma.koadooo.i18n;

import java.util.List;
import ma.koadooo.i18n.internal.LanguageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** API du module i18n, utilisable par les autres modules (ex. notification pour choisir un modèle). */
@Service
public class Languages {

    private final LanguageRepository repository;

    Languages(LanguageRepository repository) {
        this.repository = repository;
    }

    /** Langues actives, la langue par défaut en premier (US-039). */
    @Transactional(readOnly = true)
    public List<LanguageView> active() {
        return repository.findByActiveTrueOrderByDefaultLanguageDescCodeAsc().stream()
                .map(l -> new LanguageView(
                        l.getCode(), l.getNativeName(), l.getDirection().name(), l.isDefaultLanguage()))
                .toList();
    }
}
