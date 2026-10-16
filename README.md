# Kadoo — plateforme de cartes cadeaux digitales

Marketplace marocaine B2B2C : les partenaires publient des cartes cadeaux, les acheteurs les paient et les offrent
par email ou WhatsApp, les bénéficiaires les utilisent chez le partenaire. MAD · français, arabe (RTL), anglais.

> État : **Sprint 0** — socle technique, garde-fous NFR et outillage IA en place ; premier module pilote `i18n`
> (US-039). Les US métier démarrent au Lot 1 (voir backlog).
>
> Backlog : [Jira KAN](https://imadeddineb.atlassian.net/jira/software/projects/KAN) — 13 epics, 60 stories ·
> correspondance `US-xxx` ↔ `KAN-yy` dans [`docs/backlog/jira-mapping.md`](docs/backlog/jira-mapping.md).

## Démarrer

Prérequis : Java 21, Maven 3.9+, Node.js 22+, Docker. Poste complet, IDE et licences Claude :
[`docs/dev/environnement-dev.md`](docs/dev/environnement-dev.md).

```bash
docker compose up -d                         # PostgreSQL, Mailpit (http://localhost:8025), WireMock (PSP simulé)

cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
# → http://localhost:8080/api/v1/public/languages · http://localhost:8080/swagger-ui.html

cd ../frontend
npm install
npm run dev                                  # → http://localhost:3000 (redirige vers /fr, /ar ou /en)
```

## Vérifier avant une PR

```bash
cd backend  && mvn spotless:apply && mvn verify
cd frontend && npm run lint && npm run typecheck && npm test && npm run build && npm run test:e2e
```

## Organisation

```
backend/      Java 21 · Spring Boot 4 · monolithe modulaire (ma.kadoo.<module>)
frontend/     Next.js 16 · TypeScript strict · App Router · routes /[lang]
contracts/    openapi.yaml — contrat d'API unique (source de vérité)
docs/
  backlog/    Kadoo_Backlog_MVP_v1.2.xlsx + jira-mapping.md (US ↔ KAN)
  adr/        décisions d'architecture
  ai/         politique d'usage de l'IA (dont règles Jira / MCP)
  dev/        environnement de développement : outils, IDE, licences Claude
  nfr/        traçabilité NFR → code / tests / CI
infra/        simulateurs locaux (WireMock)
.claude/      commandes réutilisables pour l'agent de code
.github/      CI (backend, frontend, sécurité), modèles de PR et d'US, CODEOWNERS, Dependabot
```

## Comment on travaille

- **Une US = une branche** `feat/US-xxx-…`, des commits `US-xxx: …`, une PR avec le modèle fourni.
- **Contrat d'abord** : on modifie `contracts/openapi.yaml`, puis `npm run gen:api`, puis on implémente.
- **Tests d'abord** : chaque critère d'acceptation devient un test avant le code.
- **IA** : assistants autorisés selon `docs/ai/politique-usage-ia.md`. Les agents lisent `CLAUDE.md` et disposent de
  commandes dans `.claude/commands/` :
  - `/implement-us US-039` — flux complet plan → tests → code → vérification
  - `/tests-from-ac US-019` — tests des critères d'acceptation seulement
  - `/security-review` — revue de sécurité des changements
  - `/rtl-check src/app/[lang]/page.tsx` — contrôle arabe / RTL
- **Revue humaine obligatoire** sur chaque PR ; double revue sur paiement, émission, utilisation, commission,
  sécurité et migrations (`.github/CODEOWNERS`).

## Garde-fous automatiques (extraits)

| Garde-fou | NFR |
|---|---|
| Frontières de modules (Spring Modulith) et règles ArchUnit (pas de `double`, pas d'`Instant.now()`, pas de `System.out`) | NFR-040, NFR-014, NFR-015 |
| Audit en écriture seule garanti par la base (trigger) | US-038 |
| Catalogues fr / ar / en identiques, `dir="rtl"` testé, axe WCAG 2.1 AA | NFR-080, NFR-081, NFR-070 |
| Contrat OpenAPI et types frontend synchronisés | NFR-041 |
| Secrets (gitleaks), dépendances vulnérables, Dependabot | NFR-004, NFR-007 |
| Couverture ≥ 60 % global, ≥ 80 % modules critiques | NFR-042 |

Détail complet : [`docs/nfr/tracabilite.md`](docs/nfr/tracabilite.md).

## Réglages GitHub à activer

- Protection de `main` : PR obligatoire, 1 approbation minimum, revue des Code Owners, checks `backend`,
  `frontend` et `security` requis, pas de push direct.
- Dependabot alerts et secret scanning.
