# Kadoo — contexte pour les agents IA (et les humains)

Ce fichier est lu par les assistants de code au début de chaque session. Il résume ce qu'il faut savoir pour
contribuer sans casser les règles du projet. Les détails sont dans `docs/`.

## Le produit

Plateforme marocaine B2B2C de cartes cadeaux 100 % digitales. Un partenaire publie une offre, un acheteur
(sans compte) paie et offre une carte, le bénéficiaire la reçoit par email ou WhatsApp et l'utilise chez le
partenaire. Kadoo prélève une commission de 3 à 10 %. Devise MAD, langues fr-MA (défaut), ar-MA (RTL), en.

- **Jira** projet `KAN` (serveur MCP `atlassian`, voir `.mcp.json`) : les 60 stories avec leurs critères
  d'acceptation **en vigueur** et leur statut. Correspondance `US-xxx` ↔ `KAN-yy` : `docs/backlog/jira-mapping.md`.
- Backlog Excel : `docs/backlog/Kadoo_Backlog_MVP_v1.2.xlsx` — référence pour les décisions (onglet 06),
  le modèle de données (05), les cycles de vie (04), les NFR (07) et le SDLC IA (09).
- Une US = un identifiant `US-xxx` (libellé Jira) = une clé `KAN-yy`. Pas de code sans US ; pas de règle métier inventée.
- En cas d'écart entre Jira et l'Excel sur les critères d'acceptation, **Jira fait foi** : le signaler au PO.

## Le dépôt

| Dossier | Contenu | Commandes |
|---|---|---|
| `backend/` | Java 21, Spring Boot 4, monolithe modulaire | `mvn verify` (tests + IT + quality gates) |
| `frontend/` | Next.js 16, TypeScript strict, App Router | `npm run lint && npm run typecheck && npm test && npm run build` |
| `contracts/openapi.yaml` | Contrat d'API unique (source de vérité) | `cd frontend && npm run gen:api` |
| `docs/adr/` | Décisions d'architecture | une ADR par décision structurante |
| `docs/design/` | Design System (tokens, règles UI, maquettes par US) | lire avant tout écran |
| `docker-compose.yml` | PostgreSQL, Mailpit (emails), WireMock (simulateur PSP) | `docker compose up -d` |

## Règles non négociables

1. **Isolation partenaire** (NFR-003) : le `partnerId` vient toujours de la session, jamais de la requête.
   Chaque endpoint back-office a un test d'accès croisé (partenaire A → ressource de B = 404).
2. **Argent** (NFR-014) : `Money` / `BigDecimal`, 2 décimales, `HALF_UP`. Jamais `double`/`float`
   (règle ArchUnit). Dans l'API : chaîne décimale `"1250.00"`. Aucun calcul monétaire dans le frontend.
3. **Temps** (NFR-015) : stockage UTC, calculs en `Africa/Casablanca` via `BusinessTime`. Jamais
   `Instant.now()` : injecter `Clock` (règle ArchUnit).
4. **Idempotence** (NFR-010/011) : toute opération rejouable (webhook PSP, émission, envoi) est protégée par
   une contrainte d'unicité en base, pas seulement par du code.
5. **Secrets** (NFR-004) : codes d'utilisation et jetons stockés en HMAC/hash ; rien de sensible dans les
   logs, l'audit, les prompts ou les tests.
6. **Audit** (US-038) : chaque action métier appelle `AuditLog.record(...)` dans sa transaction.
7. **i18n** (NFR-080/081) : aucune chaîne en dur dans le frontend ; les 3 catalogues ont les mêmes clés
   (test automatique) ; vérifier chaque écran en `/ar` (RTL).
8. **Contrat** (NFR-041) : modifier `contracts/openapi.yaml` d'abord, régénérer le client, puis implémenter.
9. **Schéma** (NFR-043) : migrations Flyway uniquement ; ne jamais modifier une migration déjà fusionnée.

## Zones sensibles — double revue humaine obligatoire

`backend/**/payment/**`, `issuance/**`, `redemption/**`, `shared/money/**`, `identity/**`, `platform/SecurityConfig*`,
`db/migration/**`. L'IA peut proposer, un senior + un second relecteur valident (voir `.github/CODEOWNERS`).

## Architecture backend

Un package par module sous `ma.kadoo` : `identity`, `partner`, `catalog`, `order`, `payment`, `issuance`,
`redemption`, `notification`, `reporting`, `audit`, `i18n` (+ `shared` ouvert, `platform` technique).
Les classes du package racine d'un module sont son API ; le reste va dans `<module>.internal`.
`ModularityTests` (Spring Modulith) et `ArchitectureRulesTest` (ArchUnit) échouent sinon.
Tests : `*Test` = unitaires, `*IT` = intégration avec PostgreSQL réel (Testcontainers).

## Architecture frontend

Toutes les routes sous `src/app/[lang]/` (`fr`, `ar`, `en`). Textes dans `src/app/[lang]/dictionaries/*.json`,
lus côté serveur via `getDictionary()`. `src/proxy.ts` redirige vers la bonne langue. Formatage via
`src/lib/format.ts`. Types d'API générés dans `src/lib/api/schema.d.ts` (ne pas éditer à la main).
Lire `frontend/AGENTS.md` : cette version de Next.js diffère de ce que les modèles connaissent.

## Flux de travail par US (onglet 09 du backlog)

1. Lire la story dans Jira via le MCP (`KAN-yy`, ou recherche par libellé `US-xxx`) : description, RG, AC,
   dépendances, décisions liées. Si une décision est ouverte ou si une dépendance n'est pas livrée, s'arrêter et demander.
2. Proposer un plan (fichiers, migrations, endpoints, tests) et le faire valider ; une fois validé, le poster en
   commentaire sur la story et, si le développeur le demande, passer la story « In Progress ».
3. Écrire les tests des critères d'acceptation **avant** le code.
4. Implémenter jusqu'au vert, sans désactiver ni affaiblir un test ou une règle.
5. Lancer les commandes de vérification ci-dessus.
6. Branche `feat/US-xxx-description`, commits `US-xxx: …`, PR `US-xxx (KAN-yy) — titre` avec le modèle
   (assistance IA déclarée) ; commenter la story avec le lien de la PR.

Dans Jira, l'agent ne passe jamais une story à « Done », ne crée ni ne supprime de story et ne modifie jamais
ses critères d'acceptation (`docs/ai/politique-usage-ia.md`, §7).

## Interdits

- Données réelles (production, personnes, RIB) dans le code, les tests, les fixtures ou les prompts.
- Désactiver un contrôle de sécurité, une règle ArchUnit ou un seuil de couverture pour « faire passer » la CI.
- Cryptographie maison : bibliothèques éprouvées uniquement.
- Ajouter une fonctionnalité hors backlog sans validation du PO.
