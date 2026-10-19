package ma.kadoo.audit;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ma.kadoo.audit.internal.AuditEvent;
import ma.kadoo.audit.internal.AuditEventRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * API du module audit (KAN-65). Appelée dans la transaction de l'action métier :
 * si l'action est annulée, l'événement l'est aussi.
 */
@Service
public class AuditLog {

    public enum ActorType {
        USER,
        SYSTEM,
        PSP
    }

    public record Entry(
            String entityType,
            String entityId,
            String eventType,
            ActorType actorType,
            String actorId,
            UUID partnerId,
            String changesJson,
            String ip) {

        public Entry(
                String entityType,
                String entityId,
                String eventType,
                ActorType actorType,
                String actorId,
                UUID partnerId,
                String changesJson) {
            this(entityType, entityId, eventType, actorType, actorId, partnerId, changesJson, null);
        }
    }

    /** Clé JSON de {@code changes} : "nom": */
    private static final Pattern JSON_KEY = Pattern.compile("\"([^\"\\\\]+)\"\\s*:");

    private static final List<String> FRAGMENTS_INTERDITS =
            List.of("password", "token", "secret", "usagecode", "redemptioncode");

    private static final Set<String> CLES_INTERDITES = Set.of("iban", "rib", "cvv");

    private final AuditEventRepository repository;
    private final Clock clock;

    AuditLog(AuditEventRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public UUID record(Entry entry) {
        refuseLesSecrets(entry.changesJson());
        AuditEvent saved = repository.save(new AuditEvent(
                entry.entityType(),
                entry.entityId(),
                entry.eventType(),
                entry.actorType().name(),
                entry.actorId(),
                entry.partnerId(),
                entry.changesJson(),
                entry.ip(),
                MDC.get("traceId"),
                clock.instant()));
        return saved.getId();
    }

    /**
     * Filet de sécurité de la RG2 (KAN-65) : refuse les champs dont le nom désigne un secret (mot de passe, code
     * d'utilisation, jeton, donnée bancaire). Seules les clés sont inspectées, jamais les valeurs : la garantie
     * reste que l'appelant ne transmette pas de secret. Le message ne reprend aucune valeur.
     */
    private static void refuseLesSecrets(String changesJson) {
        if (changesJson == null) {
            return;
        }
        Matcher cles = JSON_KEY.matcher(changesJson);
        while (cles.find()) {
            String cle = cles.group(1).toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
            if (CLES_INTERDITES.contains(cle) || FRAGMENTS_INTERDITS.stream().anyMatch(cle::contains)) {
                throw new IllegalArgumentException("Champ secret interdit dans le journal d'audit : " + cles.group(1));
            }
        }
    }
}
