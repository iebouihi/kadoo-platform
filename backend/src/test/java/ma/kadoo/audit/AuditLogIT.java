package ma.kadoo.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import ma.kadoo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/** US-038 : le journal d'audit est en écriture seule, garanti par la base elle-même. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AuditLogIT {

    @Autowired
    AuditLog auditLog;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TransactionTemplate tx;

    @Test
    void enregistre_un_evenement_puis_refuse_sa_modification_et_sa_suppression() {
        UUID id = tx.execute(status -> auditLog.record(new AuditLog.Entry(
                "Language", "fr-MA", "LANGUAGE_VIEWED", AuditLog.ActorType.SYSTEM, null, null, null)));

        assertThat(jdbc.queryForObject("select count(*) from audit_event where id = ?", Long.class, id))
                .isEqualTo(1L);
        assertThatThrownBy(() -> jdbc.update("update audit_event set event_type = 'X' where id = ?", id))
                .hasMessageContaining("écriture seule");
        assertThatThrownBy(() -> jdbc.update("delete from audit_event where id = ?", id))
                .hasMessageContaining("écriture seule");
    }

    @Test
    void exige_une_transaction_metier() {
        assertThatThrownBy(() -> auditLog.record(new AuditLog.Entry(
                        "Language", "fr-MA", "X", AuditLog.ActorType.SYSTEM, null, null, null)))
                .isInstanceOf(org.springframework.transaction.IllegalTransactionStateException.class);
    }
}
