-- US-039 : référentiel des langues. fr-MA est la langue par défaut (fallback US-045).
CREATE TABLE language (
    code        VARCHAR(10)  PRIMARY KEY,
    native_name VARCHAR(100) NOT NULL,
    direction   VARCHAR(3)   NOT NULL CHECK (direction IN ('LTR', 'RTL')),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    is_default  BOOLEAN      NOT NULL DEFAULT FALSE
);

-- Une seule langue par défaut
CREATE UNIQUE INDEX language_single_default ON language (is_default) WHERE is_default;

INSERT INTO language (code, native_name, direction, is_active, is_default) VALUES
    ('fr-MA', 'Français', 'LTR', TRUE, TRUE),
    ('ar-MA', 'العربية',  'RTL', TRUE, FALSE),
    ('en',    'English',  'LTR', TRUE, FALSE);
