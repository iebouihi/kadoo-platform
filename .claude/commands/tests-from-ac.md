---
description: Générer les tests à partir des critères d'acceptation d'une US (sans implémenter)
argument-hint: US-xxx ou KAN-yy
---

Pour **$ARGUMENTS**, lis d'abord la story dans Jira (serveur MCP `atlassian`, projet `KAN` ; correspondance dans
`docs/backlog/jira-mapping.md`) : ce sont ses critères d'acceptation qui font foi. Puis écris uniquement les tests,
sans code de production :

- Un test par critère d'acceptation (AC1, AC2…), nommé en français d'après le comportement attendu.
- Les cas d'erreur de chaque règle de gestion (RG).
- Backend : `*Test` pour la logique pure, `*IT` avec `TestcontainersConfiguration` pour la base et l'HTTP.
  Pour paiement / émission / utilisation : un test de concurrence (≥ 50 appels parallèles).
- Frontend : Vitest pour la logique, Playwright pour les parcours (fr et ar), axe pour l'accessibilité.
- Données synthétiques uniquement.

Termine par une matrice `AC → test` et la liste des AC non testables automatiquement (à faire en recette).
Propose ensuite de poster cette matrice en commentaire sur la story Jira (sans modifier ses critères).
