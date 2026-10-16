-- US-038 / NFR-015 : journal d'audit en écriture seule.
CREATE TABLE audit_event (
    id             UUID         PRIMARY KEY,
    entity_type    VARCHAR(60)  NOT NULL,
    entity_id      VARCHAR(100) NOT NULL,
    event_type     VARCHAR(80)  NOT NULL,
    actor_type     VARCHAR(10)  NOT NULL CHECK (actor_type IN ('USER', 'SYSTEM', 'PSP')),
    actor_id       VARCHAR(100),
    partner_id     UUID,
    changes        TEXT,
    correlation_id VARCHAR(64),
    occurred_at    TIMESTAMPTZ  NOT NULL
);

CREATE INDEX audit_event_entity ON audit_event (entity_type, entity_id, occurred_at);
CREATE INDEX audit_event_partner ON audit_event (partner_id, occurred_at);

-- Toute modification ou suppression est refusée par la base, quel que soit le code applicatif.
CREATE FUNCTION audit_event_is_append_only() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'audit_event est en écriture seule (US-038)';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER audit_event_no_update_or_delete
    BEFORE UPDATE OR DELETE ON audit_event
    FOR EACH ROW EXECUTE FUNCTION audit_event_is_append_only();

CREATE TRIGGER audit_event_no_truncate
    BEFORE TRUNCATE ON audit_event
    FOR EACH STATEMENT EXECUTE FUNCTION audit_event_is_append_only();
