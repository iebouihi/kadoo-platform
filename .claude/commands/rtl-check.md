---
description: Vérifier qu'un écran fonctionne en arabe (RTL) et dans les 3 langues
argument-hint: chemin de la page, ex. src/app/[lang]/page.tsx
---

Vérifie **$ARGUMENTS** :

1. Aucune chaîne en dur : tout passe par `getDictionary()` ; les clés existent dans `fr.json`, `ar.json`, `en.json`.
2. Propriétés CSS logiques uniquement (`ms-`, `me-`, `ps-`, `pe-`, `start-`, `end-`, `text-start`) ; aucune `ml-`, `mr-`, `left-`, `right-`, `text-left`.
3. Icônes directionnelles (flèches, chevrons) inversées en RTL.
4. Montants, dates, références, codes, numéros de téléphone dans `<bdi>` ; formatage via `src/lib/format.ts`.
5. Un test Playwright couvre la page en `/ar` (attribut `dir="rtl"`) et passe axe sans violation.

Liste les corrections, puis applique-les si on te le demande.
