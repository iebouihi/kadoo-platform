# ADR-0005 — En-têtes de sécurité et CSP du frontend

- Statut : acceptée partiellement (Sprint 0)
- Date : 2026-10-16

## Contexte

Une CSP stricte avec nonce impose le rendu dynamique de chaque page, ce qui s'oppose au prérendu des pages
publiques exigé par NFR-020.

## Décision

- Toutes les réponses : HSTS, `nosniff`, `Referrer-Policy`, `Permissions-Policy` (caméra autorisée pour le scan QR),
  CSP de base `frame-ancestors 'none'; object-src 'none'; base-uri 'self'; form-action 'self'` (`next.config.ts`).
- Back-office (pages dynamiques) : CSP complète avec nonce générée dans `src/proxy.ts`, à ajouter avec US-001.
- Pages publiques : `script-src` restreint par hash ou SRI, à décider après mesure de performance.

## Conséquences

Les en-têtes de base sont vérifiés par un test Playwright. Le durcissement complet est un critère de sortie du Lot 1.
