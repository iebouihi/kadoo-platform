# ADR-0007 — Design System et maquettes

- Statut : acceptée (Sprint 0)
- Date : 2026-10-16

## Contexte

Le produit est public, mobile d'abord, en trois langues dont l'arabe (RTL). Sans référence commune, chaque écran
(et chaque agent IA) invente ses couleurs et ses composants, et les NFR d'accessibilité et de RTL se vérifient au cas par cas.

## Décision

- Un **Design System Kadoo** (tokens, typographie latin + arabe, 8 composants, guide de marque) et des **maquettes du
  parcours MVP** sont tenus sur claude.ai, où l'équipe les consulte et les commente (`docs/design/README.md`).
- Le **dépôt reste la source pour le code** : `docs/design/tokens.json`, `globals.css` (Tailwind v4 `@theme`),
  `frontend/src/components/ui/`, polices auto-hébergées. Un test empêche `tokens.json` et `globals.css` de diverger.
- Les composants du dépôt sont réécrits en composants serveur TypeScript ; le bundle d'aperçu du Design System n'est pas
  importé (React par CDN, incompatible avec les composants serveur).
- Règles UI pour les humains et les agents dans `frontend/CLAUDE.md`, `/implement-us` et `/rtl-check` ; chaque story
  avec écran pointe vers son écran de maquette.

## Conséquences

- Toute évolution visuelle passe par le Design System puis par une PR qui met à jour tokens, CSS et composants ensemble.
- Les écrans non maquettés (erreurs, back-offices) sont à concevoir avant ou pendant leurs US.
- Les textes arabes du Design System et des maquettes doivent être relus par un locuteur natif.
