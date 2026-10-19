---
description: Implémenter une user story du backlog Kadoo (Jira KAN) selon le flux tests d'abord
argument-hint: KAN-yy
---

Implémente la user story **$ARGUMENTS** en suivant strictement le flux de `CLAUDE.md`.

1. **Lire la story dans Jira** avec le serveur MCP `atlassian` (projet `KAN`), directement par sa clé `KAN-yy`.
   Récupérer description, attributs, règles de gestion (RG), critères d'acceptation (AC), dépendances, décisions liées,
   statut et commentaires récents. Si le MCP n'est pas connecté, demander au développeur de lancer `/mcp`.
   - Si un AC diffère du fichier Excel, **Jira fait foi** : le signaler.
   - Si une décision liée (libellé `D-xx`, onglet 06 de l'Excel) est « Ouverte » et change le comportement,
     **arrête-toi** et liste les questions pour le PO. N'invente aucune règle.
   - Vérifie dans Jira que les stories dont elle dépend sont terminées ; sinon, signale-le.
2. **Lire** les NFR applicables (onglet 07) et le cycle de vie des entités concernées (onglet 04).
3. **Proposer un plan** et attendre la validation : modifications de `contracts/openapi.yaml`, migrations Flyway,
   classes par module (API / internal), écrans et clés de traduction, tests à écrire. Indique si l'US touche une
   zone sensible (double revue).
   Une fois le plan validé : le poster en commentaire sur la story ; proposer de passer la story « In Progress ».
4. **Tests d'abord** : un test par AC (nommé d'après l'AC), plus les cas d'erreur des RG. Ils doivent échouer.
5. **Implémenter** par petites étapes jusqu'au vert. Ne jamais affaiblir un test ou une règle existante.
6. **Vérifier** : `cd backend && mvn verify` et/ou `cd frontend && npm run lint && npm run typecheck && npm test && npm run build`.
7. **Résumer** pour la PR (titre `KAN-yy — …`) : ce qui a été fait, AC ↔ tests, hypothèses, points à relire.
   Après création de la PR, commenter la story avec le lien et proposer de la passer « In Review ».

Interdit dans Jira : passer la story à « Done », créer ou supprimer des stories, modifier les critères d'acceptation.
