# ADR-0006 — Cycle de développement assisté par l'IA

- Statut : acceptée (Sprint 0)
- Date : 2026-10-16

## Décision

L'équipe utilise des assistants IA (conversationnels et agents de code) à chaque phase, selon l'onglet 09 du backlog
et `docs/ai/politique-usage-ia.md`. Le dépôt est conçu pour cela :

- **Contexte** : `CLAUDE.md` (racine, `backend/`, `frontend/`) décrit le produit, les règles et les commandes.
- **Commandes réutilisables** : `.claude/commands/` (`implement-us`, `tests-from-ac`, `security-review`, `rtl-check`).
- **Garde-fous automatiques** : les NFR critiques sont des tests (ArchUnit, Modulith, cross-tenant, catalogues i18n,
  axe, en-têtes) qui s'appliquent au code humain comme au code généré.
- **Contrôle humain** : revue humaine de 100 % des PR, double revue sur les zones sensibles (`CODEOWNERS`),
  déploiement production manuel. L'IA ne peut ni approuver ni fusionner.
- **Traçabilité** : ID d'US dans branches, commits et PR ; assistance IA déclarée dans le modèle de PR.

## Conséquences

Gains attendus sur le délai par US, mesurés dès les sprints 1-2 (onglet 09, indicateurs). Les règles sont revues à
chaque rétrospective.
