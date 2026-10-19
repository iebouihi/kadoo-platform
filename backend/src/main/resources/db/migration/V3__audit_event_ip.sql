-- US-038 : adresse IP de l'acteur (modèle AuditEvent). Donnée personnelle : durée de conservation à trancher (D-10).
-- Longueur 45 = IPv6 textuelle maximale (IPv4 mappée incluse). Les triggers d'écriture seule de V2 restent actifs.
ALTER TABLE audit_event ADD COLUMN ip VARCHAR(45);
