# ADR-0003 — Contrat OpenAPI comme source de vérité

- Statut : acceptée (Sprint 0)
- Date : 2026-10-16

## Décision

`contracts/openapi.yaml` décrit l'API avant son implémentation (contract-first). Le frontend génère ses types avec
`openapi-typescript` (`npm run gen:api`) ; la CI échoue si le fichier généré n'est pas à jour (`npm run check:api`).
Le backend expose sa documentation (springdoc) pour comparaison en recette. Préfixe `/api/v1` ; toute rupture
impose `/api/v2`. Erreurs au format RFC 9457 avec un `code` stable traduit côté frontend. Montants en chaîne décimale.

## Conséquences

- Front et back travaillent en parallèle sur un contrat validé.
- Les agents IA modifient le contrat d'abord, ce qui rend chaque changement d'API visible en revue.
- Étape suivante : test de conformité automatique du backend au contrat (à ajouter avec les premières US métier).
