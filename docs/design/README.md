# Design System Kadoo

Référence visuelle du produit. Elle existe en deux endroits :

| Où | Pour quoi | Source de vérité pour |
|---|---|---|
| **Design System** sur claude.ai : https://claude.ai/artifact/JRb34HE1icJsVPwYsrZ5xi | Voir les tokens, les composants vivants (clair / sombre), le guide de marque ; commenter | Les décisions de design |
| **Maquettes du parcours MVP** sur claude.ai : https://claude.ai/artifact/To4YkWAmEFgpHVNLZPZG8L | Voir chaque écran en fr et en ar, les commenter, cliquer le parcours | L'intention de chaque écran |
| **Ce dépôt** | Coder | Ce qui est livré |

Les liens claude.ai ne s'ouvrent que s'ils ont été partagés avec vous (menu Partager de la page). Tout ce dont un
développeur ou un agent a besoin pour coder est **dans le dépôt** : ce fichier, `tokens.json`, `frontend/src/app/[lang]/globals.css`
et `frontend/src/components/ui/`.

## Dans le code

- **Tokens** : `docs/design/tokens.json` (copie de référence) → `frontend/src/app/[lang]/globals.css` (variables CSS en
  clair et en sombre, exposées à Tailwind v4). Le test `frontend/src/design/tokens.test.ts` échoue si les deux divergent.
- **Classes Tailwind disponibles** : couleurs `surface`, `surface-raised`, `surface-sunken`, `line`, `line-strong`, `ink`,
  `ink-muted`, `majorelle`, `on-majorelle`, `majorelle-soft`, `safran`, `on-safran`, `safran-soft`, `safran-ink`, `menthe`,
  `menthe-soft`, `grenade`, `grenade-soft` (`bg-*`, `text-*`, `border-*`, `fill-*`) ; polices `font-sans`, `font-display`,
  `font-mono` ; tailles `text-display-xl`, `text-display`, `text-heading`, `text-subheading`, `text-body`, `text-body-sm`,
  `text-caption`, `text-amount`, `text-code` ; rayons `rounded-sm|md|lg|full` ; ombres `shadow-card`, `shadow-gift` ;
  hauteurs `min-h-control` (48 px), `min-h-control-sm` (44 px) ; largeurs `max-w-reading`, `max-w-content`.
- **Polices** : Reem Kufi (titres), Readex Pro (texte), IBM Plex Mono (codes), auto-hébergées dans `frontend/src/fonts/`
  (sous-ensembles latin et arabe, licence OFL), chargées par `next/font/local` (NFR-082).
- **Composants** : `frontend/src/components/ui/` — `Button` / `ButtonLink`, `TextField`, `Amount`, `CardStatus`,
  `GiftCard`, `RedemptionCode`, `LanguageSwitcher`, `Alert`, `Khatam`. Composants serveur, sans texte en dur.

## Règles

1. **Aucune couleur, police, taille ou ombre en dur** (`#2340d3`, `text-[#…]`, `font-['…']`) : uniquement les classes ci-dessus.
   Seule exception : la couleur propre d'un partenaire sur sa carte (US-008), qui vient de l'API.
2. **Composants d'abord** : utiliser `components/ui`. Un nouveau composant réutilisable se propose en revue (et dans le
   Design System) avant d'être créé ; un assemblage propre à un écran reste dans l'écran.
3. **Couleur d'action** : `majorelle` (bouton `primary`, liens, focus). **Safran** est réservé au geste d'offrir
   (bouton `gift` : « Offrir », « Payer ») et à l'étoile. Une action principale par écran.
4. **États** : couleur + symbole + mot (`CardStatus`, `Alert`). La couleur seule ne porte jamais l'information.
5. **Contenus** : vouvoiement, phrases courtes, verbe en tête sur les boutons, erreurs qui disent quoi corriger, pas d'emoji.
6. **Montants** : `Amount` (chaîne de l'API, aucun calcul) ; dates : `formatDateTime` (fuseau Africa/Casablanca).
7. **RTL** : propriétés logiques (`ms-`, `pe-`, `start-`, `end-`, `border-s`), `<bdi>` pour montants, dates, références ;
   code d'utilisation, emails et téléphones en `dir="ltr"`. Pas d'interlettrage ni de capitales en arabe.
8. **Accessibilité** : WCAG 2.1 AA (contrastes du DS déjà conformes), cibles ≥ 44 px, focus visible, vrais `<button>`,
   `<a>`, `<label>`.
9. **Sécurité** : le code d'utilisation n'apparaît en clair que sur la page cadeau ; ailleurs `RedemptionCode masked`.

## Écrans de maquette par user story

| Écran (maquettes) | User stories | Jira |
|---|---|---|
| 1 · Vitrine (fr, ar) | US-011, US-040 | KAN-30, KAN-67 |
| 2 · Détail de l'offre | US-011, US-012 | KAN-30, KAN-31 |
| 3 · Bénéficiaire et message | US-014, US-015, US-016 | KAN-33, KAN-34, KAN-35 |
| 4 · Récapitulatif | US-017, US-060 | KAN-36, KAN-76 |
| 5 · Paiement confirmé | US-018, US-025 | KAN-38, KAN-47 |
| 6 · Page cadeau (fr, ar) | US-023, US-046 | KAN-45, KAN-73 |
| 7 · Caisse : vérifier | US-026 | KAN-49 |
| 8 · Caisse : valider (fr, ar) | US-027 | KAN-50 |

Écrans pas encore maquettés : états d'erreur (offre indisponible, carte expirée / déjà utilisée en caisse, paiement en
attente ou refusé), back-office partenaire (offres, ventes, historique), back-office Admin. Une US avec écran sans
maquette : le signaler au PO avant d'implémenter ; à défaut, assembler l'écran avec `components/ui` et joindre des captures.

## Faire évoluer le Design System

1. Proposer le changement (commentaire sur le Design System ou ticket) ; validation design + PO.
2. Mettre à jour le Design System sur claude.ai, puis reporter les valeurs dans `docs/design/tokens.json` **et**
   `globals.css` dans la même PR (le test de cohérence l'impose).
3. Adapter les composants concernés et leurs tests ; vérifier les écrans en `/fr` et `/ar`.
