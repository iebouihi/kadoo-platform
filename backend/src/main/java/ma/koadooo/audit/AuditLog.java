package ma.koadooo.audit;

import java.time.Clock;
import java.util.UUID;
import ma.koadooo.audit.internal.AuditEvent;
import ma.koadooo.audit.internal.AuditEventRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * API du module audit (US-038). Appelée dans la transaction de l'action métier :
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
            String changesJson) {}

    private final AuditEventRepository repository;
    private final Clock clock;

    AuditLog(AuditEventRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public UUID record(Entry entry) {
        AuditEvent saved = repository.save(new AuditEvent(
                entry.entityType(),
                entry.entityId(),
                entry.eventType(),
                entry.actorType().name(),
                entry.actorId(),
                entry.partnerId(),
                entry.changesJson(),
                MDC.get("traceId"),
                clock.instant()));
        return saved.getId();
    }
}
