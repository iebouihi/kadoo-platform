---
description: Implémenter une user story du backlog KOADOOO selon le flux tests d'abord
argument-hint: US-xxx
---

Implémente la user story **$ARGUMENTS** en suivant strictement le flux de `CLAUDE.md`.

1. **Lire** l'US dans `docs/backlog/KOADOOO_Backlog_MVP_v1.2.xlsx` (onglet 03) ou dans Jira : description,
   attributs, règles de gestion (RG), critères d'acceptation (AC), dépendances, décisions liées.
   - Si une décision liée (onglet 06) est « Ouverte » et change le comportement, **arrête-toi** et liste les
     questions pour le PO. N'invente aucune règle.
   - Vérifie que les US dont elle dépend sont livrées ; sinon, signale-le.
2. **Lire** les NFR applicables (onglet 07) et le cycle de vie des entités concernées (onglet 04).
3. **Proposer un plan** et attendre la validation : modifications de `contracts/openapi.yaml`, migrations Flyway,
   classes par module (API / internal), écrans et clés de traduction, tests à écrire. Indique si l'US touche une
   zone sensible (double revue).
4. **Tests d'abord** : un test par AC (nommé d'après l'AC), plus les cas d'erreur des RG. Ils doivent échouer.
5. **Implémenter** par petites étapes jusqu'au vert. Ne jamais affaiblir un test ou une règle existante.
6. **Vérifier** : `cd backend && mvn verify` et/ou `cd frontend && npm run lint && npm run typecheck && npm test && npm run build`.
7. **Résumer** pour la PR : ce qui a été fait, AC ↔ tests, hypothèses, points à relire en priorité.
