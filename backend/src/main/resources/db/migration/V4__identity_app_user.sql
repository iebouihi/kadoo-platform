-- KAN-17 : comptes back-office (Admin Kadoo, gestionnaire partenaire).
-- "user" est un mot réservé en SQL : la table s'appelle app_user.
CREATE TABLE app_user (
    id                 UUID         PRIMARY KEY,
    email              VARCHAR(254) NOT NULL,
    password_hash      VARCHAR(255),                  -- Argon2id ; absent tant que le compte est INVITED (KAN-18)
    role               VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'PARTNER_MANAGER')),
    partner_id         UUID,                          -- clé étrangère vers partner ajoutée avec ce module (KAN-21)
    status             VARCHAR(10)  NOT NULL CHECK (status IN ('INVITED', 'ACTIVE', 'DISABLED')),
    failed_login_count INTEGER      NOT NULL DEFAULT 0 CHECK (failed_login_count >= 0),
    locked_until       TIMESTAMPTZ,
    last_login_at      TIMESTAMPTZ,
    created_at         TIMESTAMPTZ  NOT NULL,
    -- Un Admin n'appartient à aucun partenaire, un gestionnaire à exactement un (NFR-003).
    CONSTRAINT app_user_partner_by_role CHECK ((role = 'ADMIN') = (partner_id IS NULL)),
    CONSTRAINT app_user_active_has_password CHECK (status <> 'ACTIVE' OR password_hash IS NOT NULL)
);

-- RG1 : email unique sans tenir compte de la casse, garanti par la base.
CREATE UNIQUE INDEX app_user_email_unique ON app_user (lower(email));
CREATE INDEX app_user_partner ON app_user (partner_id);
