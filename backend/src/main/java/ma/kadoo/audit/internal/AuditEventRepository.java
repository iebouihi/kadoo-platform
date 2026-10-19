package ma.kadoo.audit.internal;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.Repository;

/** Dépôt volontairement limité à l'insertion : aucune méthode de mise à jour ni de suppression n'est exposée. */
public interface AuditEventRepository extends Repository<AuditEvent, UUID> {

    AuditEvent save(AuditEvent event);

    long count();

    /** Lecture seule : chronologie d'une entité, du plus ancien au plus récent. */
    List<AuditEvent> findByEntityTypeAndEntityIdOrderByOccurredAtAscIdAsc(String entityType, String entityId);
}
