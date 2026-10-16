@AGENTS.md

# Frontend Kadoo

Règles générales : voir `../CLAUDE.md`. Spécifique au frontend :

- **Lire la doc embarquée** de Next.js (`node_modules/next/dist/docs/`) avant d'utiliser une API : cette version
  (16.x, `proxy.ts`, Cache Components, `next/root-params`) diffère des données d'entraînement des modèles.
- **Composants serveur par défaut.** `"use client"` seulement pour l'interactivité (ex. lecteur QR US-026).
- **Aucune règle métier, aucun contrôle d'autorisation de confiance** : le frontend affiche et appelle l'API.
- **Textes** : ajouter chaque clé dans `fr.json`, `ar.json` et `en.json` (le test `config.test.ts` échoue sinon).
  Les textes arabes sont relus par un locuteur natif avant fusion.
- **RTL** : propriétés CSS logiques (`ms-*`, `me-*`, `ps-*`, `start-*`), jamais `ml-*`/`left-*` ;
  montants, dates, références dans `<bdi>`.
- **Montants** : reçus en chaîne, affichés avec `formatMoney` ; pas de `parseFloat` (règle ESLint).
- **Performance** (NFR-020) : pages publiques statiques ou partiellement prérendues ; images via `next/image`.
- **Accessibilité** (NFR-070) : chaque nouvelle page a un test axe dans `e2e/`.

Vérification avant PR : `npm run lint && npm run typecheck && npm test && npm run build && npm run test:e2e`.
