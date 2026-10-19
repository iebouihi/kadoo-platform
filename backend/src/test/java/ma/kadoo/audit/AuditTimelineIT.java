package ma.kadoo.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.kadoo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/** US-038 : chronologie d'une entité (AC1), absence de secrets dans le journal (AC2, RG2), adresse IP. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AuditTimelineIT {

    private static final String CODE_UTILISATION = "KDO-7QXM-4PLA-93ZT";

    @Autowired
    AuditLog auditLog;

    @Autowired
    AuditTimeline timeline;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TransactionTemplate tx;

    /** AC1 : l'Admin reconstitue la chronologie complète d'une entité, dans l'ordre, sans les autres entités. */
    @Test
    void ac1_restitue_la_chronologie_d_une_entite_dans_l_ordre_sans_les_autres_entites() {
        String carte = "carte-" + UUID.randomUUID();
        String autre = "carte-" + UUID.randomUUID();
        // Insertion volontairement dans le désordre : l'ordre vient de occurred_at, pas de l'insertion.
        insere(carte, "CARD_USED", "2026-03-03T10:00:00Z");
        insere(carte, "ORDER_PLACED", "2026-03-01T10:00:00Z");
        insere(autre, "ORDER_PLACED", "2026-03-02T10:00:00Z");
        insere(carte, "CARD_ISSUED", "2026-03-02T10:00:00Z");

        List<AuditTimeline.Event> events = timeline.forEntity("GiftCard", carte);

        assertThat(events)
                .extracting(AuditTimeline.Event::eventType)
                .containsExactly("ORDER_PLACED", "CARD_ISSUED", "CARD_USED");
    }

    @Test
    void ac1_renvoie_une_chronologie_vide_pour_une_entite_inconnue() {
        assertThat(timeline.forEntity("GiftCard", "inconnue-" + UUID.randomUUID()))
                .isEmpty();
    }

    /** AC2 : le code d'utilisation est introuvable dans le journal, quelle que soit la colonne. */
    @Test
    void ac2_le_code_d_utilisation_est_introuvable_dans_le_journal() {
        String carte = "carte-" + UUID.randomUUID();
        tx.executeWithoutResult(status -> auditLog.record(new AuditLog.Entry(
                "GiftCard", carte, "CARD_ISSUED", AuditLog.ActorType.SYSTEM, null, null, "{\"status\":\"ISSUED\"}")));

        Long trouves = jdbc.queryForObject(
                "select count(*) from audit_event where audit_event::text like ?",
                Long.class,
                "%" + CODE_UTILISATION + "%");

        assertThat(trouves).isZero();
    }

    /** RG2 : une tentative de journaliser un secret est refusée, et rien n'est écrit. */
    @Test
    void rg2_refuse_de_journaliser_un_secret() {
        for (String cle : List.of("usageCode", "password", "passwordHash", "token", "iban", "rib")) {
            String carte = "carte-" + UUID.randomUUID();
            assertThatThrownBy(() -> tx.executeWithoutResult(status -> auditLog.record(new AuditLog.Entry(
                            "GiftCard",
                            carte,
                            "CARD_USED",
                            AuditLog.ActorType.USER,
                            "u1",
                            null,
                            "{\"" + cle + "\":\"" + CODE_UTILISATION + "\"}"))))
                    .as("clé interdite : %s", cle)
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(timeline.forEntity("GiftCard", carte))
                    .as("aucune écriture pour %s", cle)
                    .isEmpty();
        }
    }

    @Test
    void rg2_accepte_un_changement_sans_secret() {
        String carte = "carte-" + UUID.randomUUID();

        tx.executeWithoutResult(status -> auditLog.record(new AuditLog.Entry(
                "GiftCard",
                carte,
                "CARD_USED",
                AuditLog.ActorType.USER,
                "u1",
                null,
                "{\"status\":{\"from\":\"ISSUED\",\"to\":\"USED\"},\"code\":\"fr-MA\"}")));

        assertThat(timeline.forEntity("GiftCard", carte)).hasSize(1);
    }

    @Test
    void enregistre_l_adresse_ip_de_l_acteur() {
        String compte = "user-" + UUID.randomUUID();

        tx.executeWithoutResult(status -> auditLog.record(new AuditLog.Entry(
                "User", compte, "LOGIN_LOCKED", AuditLog.ActorType.USER, compte, null, null, "203.0.113.7")));

        assertThat(jdbc.queryForObject("select ip from audit_event where entity_id = ?", String.class, compte))
                .isEqualTo("203.0.113.7");
    }

    private void insere(String entityId, String eventType, String occurredAt) {
        jdbc.update(
                "insert into audit_event (id, entity_type, entity_id, event_type, actor_type, occurred_at)"
                        + " values (?, 'GiftCard', ?, ?, 'SYSTEM', ?)",
                UUID.randomUUID(),
                entityId,
                eventType,
                java.sql.Timestamp.from(Instant.parse(occurredAt)));
    }
}
