package ma.kadoo.audit;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.kadoo.audit.internal.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lecture du journal d'audit (US-038, AC1) : chronologie d'une entité, de l'événement le plus ancien au plus récent.
 * Aucun endpoint HTTP pour l'instant : la consultation Admin arrive avec US-001 (rôle ADMIN).
 */
@Service
public class AuditTimeline {

    /** Vue d'un événement : volontairement sans {@code changes} ni {@code ip} (à exposer par l'écran Admin). */
    public record Event(UUID id, String eventType, AuditLog.ActorType actorType, String actorId, Instant occurredAt) {}

    private final AuditEventRepository repository;

    AuditTimeline(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Event> forEntity(String entityType, String entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByOccurredAtAscIdAsc(entityType, entityId).stream()
                .map(e -> new Event(
                        e.getId(),
                        e.getEventType(),
                        AuditLog.ActorType.valueOf(e.getActorType()),
                        e.getActorId(),
                        e.getOccurredAt()))
                .toList();
    }
}
