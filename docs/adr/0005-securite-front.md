# ADR-0005 — En-têtes de sécurité et CSP du frontend

- Statut : acceptée partiellement (Sprint 0) ; CSP back-office en place (KAN-17)
- Date : 2026-10-16

## Contexte

Une CSP stricte avec nonce impose le rendu dynamique de chaque page, ce qui s'oppose au prérendu des pages
publiques exigé par NFR-020.

## Décision

- Toutes les réponses : HSTS, `nosniff`, `Referrer-Policy`, `Permissions-Policy` (caméra autorisée pour le scan QR),
  CSP de base `frame-ancestors 'none'; object-src 'none'; base-uri 'self'; form-action 'self'` (`next.config.ts`).
- Back-office (`/login`, `/admin`, `/partner`) : CSP complète avec nonce par requête générée dans `src/proxy.ts`
  (`src/lib/backoffice/csp.ts`) ; ces pages sont rendues à chaque requête (`instant = false`), sinon leurs scripts
  prérendus n'auraient pas de nonce et seraient bloqués (vérifié par `e2e/back-office.spec.ts`).
- Pages publiques : `script-src` restreint par hash ou SRI, à décider après mesure de performance.

## Conséquences

Les en-têtes de base sont vérifiés par un test Playwright. Le durcissement complet est un critère de sortie du Lot 1.
