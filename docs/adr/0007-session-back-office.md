# ADR-0007 — Session back-office : cookie serveur et API en même origine

- Statut : acceptée (KAN-17)
- Date : 2026-10-19

## Contexte

KAN-17 et NFR-002 imposent une session back-office dans un cookie HttpOnly, Secure, SameSite=Lax, sans jeton dans
le navigateur (pas de localStorage), avec inactivité de 30 min, durée maximale de 8 h et protection CSRF des
mutations. Le frontend (Next.js) et le backend (Spring Boot) sont deux processus distincts.

## Décision

- **Session côté serveur** : Spring Session JDBC (tables créées par Flyway, V5). Le cookie `SESSION` ne contient
  qu'un identifiant opaque ; le contenu reste en base, partagé entre instances. Identifiant renouvelé à la connexion.
- **Même origine** : le navigateur appelle `/api/v1/**` sur l'origine du frontend ; `next.config.ts` réécrit ces
  appels vers le backend (`KADOO_API_URL`, connue du seul serveur Next.js). Pas de CORS, cookie SameSite=Lax.
- **Pages back-office** : rendues à chaque requête ; le serveur Next.js transmet le cookie au backend
  (`GET /auth/me`) et redirige vers la connexion sans session valide. Ce n'est qu'une commodité : chaque appel
  d'API est contrôlé par le backend (rôle et partenaire, NFR-003).
- **CSRF** : jeton dans le cookie lisible `XSRF-TOKEN`, recopié dans l'en-tête `X-XSRF-TOKEN`
  (`csrf.spa()` de Spring Security), y compris pour la connexion elle-même.
- **Durées** : inactivité par Spring Session (`server.servlet.session.timeout`), durée maximale par un filtre du
  module identity placé entre Spring Session et Spring Security.

## Conséquences

- Aucun secret ni jeton accessible au JavaScript du navigateur ; révocation possible en supprimant la ligne de
  session (désactivation d'un compte, KAN-19).
- Les pages back-office ne sont pas prérendues (`instant = false`) : c'est aussi ce qu'exige la CSP avec nonce
  (ADR-0005). Les pages publiques restent statiques (NFR-020).
- Une redirection décidée pendant le rendu est transmise dans le flux (balise `meta refresh` et instruction RSC),
  avec un statut 200 : comportement de Next.js avec Cache Components, sans conséquence de sécurité.
- Plusieurs instances du frontend ou du backend partagent la même session sans configuration supplémentaire.
