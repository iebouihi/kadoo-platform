# ADR-0002 — Frontend Next.js unique pour le public et le back-office

- Statut : acceptée (Sprint 0)
- Date : 2026-10-16

## Décision

Une application Next.js 16 (App Router, TypeScript strict) sert les pages publiques (catalogue, achat, page cadeau)
et le back-office (Admin, partenaire). Routage par langue `/fr`, `/ar`, `/en` (`src/app/[lang]`), dictionnaires JSON
chargés côté serveur avec `next/root-params`, redirection de langue dans `src/proxy.ts`.

Pas de bibliothèque i18n tierce au démarrage : l'approche native documentée par Next.js suffit et reste compatible
avec Cache Components. À réévaluer si le besoin de pluriels / formats complexes apparaît.

## Conséquences

- Pages publiques prérendues : performance (NFR-020) et aperçus de partage (US-012).
- Le frontend ne contient aucune règle métier ni contrôle d'autorisation de confiance (NFR-003).
- Image Docker `standalone`.
