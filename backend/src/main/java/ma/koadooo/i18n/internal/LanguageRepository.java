package ma.koadooo.i18n.internal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, String> {

    List<Language> findByActiveTrueOrderByDefaultLanguageDescCodeAsc();
}
