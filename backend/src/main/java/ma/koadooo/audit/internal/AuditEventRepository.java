package ma.koadooo.audit.internal;

import java.util.UUID;
import org.springframework.data.repository.Repository;

/** Dépôt volontairement limité à l'insertion : aucune méthode de mise à jour ni de suppression n'est exposée. */
public interface AuditEventRepository extends Repository<AuditEvent, UUID> {

    AuditEvent save(AuditEvent event);

    long count();
}
