package ma.kadoo.audit.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Événement d'audit (US-038). Écriture seule : la base refuse UPDATE et DELETE (migration V2).
 * Ne jamais y mettre de secret : mot de passe, code d'utilisation, jeton, donnée bancaire.
 */
@Entity
@Table(name = "audit_event")
public class AuditEvent {

    @Id
    private UUID id;

    @Column(name = "entity_type", nullable = false, updatable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false, updatable = false)
    private String entityId;

    @Column(name = "event_type", nullable = false, updatable = false)
    private String eventType;

    @Column(name = "actor_type", nullable = false, updatable = false)
    private String actorType;

    @Column(name = "actor_id", updatable = false)
    private String actorId;

    @Column(name = "partner_id", updatable = false)
    private UUID partnerId;

    @Column(name = "changes", updatable = false)
    private String changes;

    @Column(name = "ip", updatable = false)
    private String ip;

    @Column(name = "correlation_id", updatable = false)
    private String correlationId;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    protected AuditEvent() {}

    public AuditEvent(
            String entityType,
            String entityId,
            String eventType,
            String actorType,
            String actorId,
            UUID partnerId,
            String changes,
            String ip,
            String correlationId,
            Instant occurredAt) {
        this.id = UUID.randomUUID();
        this.entityType = entityType;
        this.entityId = entityId;
        this.eventType = eventType;
        this.actorType = actorType;
        this.actorId = actorId;
        this.partnerId = partnerId;
        this.changes = changes;
        this.ip = ip;
        this.correlationId = correlationId;
        this.occurredAt = occurredAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getActorType() {
        return actorType;
    }

    public String getActorId() {
        return actorId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
