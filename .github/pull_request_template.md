## User story

US-xxx — <!-- titre -->  · Jira : <!-- lien -->

## Ce qui change

<!-- 3 à 5 lignes : comportement, API, migrations, écrans -->

## Critères d'acceptation → tests

| AC | Test |
|---|---|
| AC1 | |

## Assistance IA

- [ ] Aucune
- [ ] Oui — outil : <!-- ex. Claude Code --> ; parties concernées : <!-- code, tests, doc… -->
- [ ] J'ai relu et je comprends chaque ligne générée

## Vérifications

- [ ] Contrat `contracts/openapi.yaml` mis à jour avant l'implémentation (si l'API change)
- [ ] Migration Flyway nouvelle (aucune migration existante modifiée)
- [ ] Textes ajoutés en fr, ar et en ; écran vérifié en `/ar`
- [ ] Écran conforme à sa maquette, tokens et composants `ui` uniquement ; captures mobile fr + ar jointes
- [ ] Aucune donnée réelle, aucun secret, aucun code d'utilisation dans les logs / l'audit
- [ ] Événements d'audit émis pour les actions métier
- [ ] Zone sensible touchée (paiement, émission, utilisation, commission, sécurité) → double revue demandée
- [ ] `docs/nfr/tracabilite.md` mis à jour si une NFR passe « en place »
