# Traçabilité NFR → dépôt

Où chaque exigence non fonctionnelle (onglet 07 du backlog) est appliquée ou vérifiée. « À venir » = prévu avec
l'US indiquée ; la ligne doit passer à « en place » dans la PR correspondante.

| NFR | Exigence | Où | État |
|---|---|---|---|
| NFR-001 | En-têtes de sécurité | `frontend/next.config.ts`, `backend/.../platform/SecurityConfig.java`, test `e2e/i18n.spec.ts` | en place (base) ; CSP nonce avec US-001 |
| NFR-002 | Sessions back-office | `SecurityConfig` | à venir (US-001) |
| NFR-003 | Isolation partenaire | règle dans `CLAUDE.md`, tests cross-tenant | à venir (US-002) |
| NFR-004 | Secrets | `.github/workflows/security.yml` (gitleaks) | en place |
| NFR-005 | Webhook PSP signé | route `/api/v1/webhooks/**` réservée dans `SecurityConfig` | à venir (US-018) |
| NFR-006 | Limitation de débit | — | à venir (US-001, US-052) |
| NFR-007 | Chaîne logicielle | `.github/dependabot.yml`, dependency-review dans `security.yml` | en place |
| NFR-008 | Contenus utilisateur | ESLint `react/no-danger` | en place (front) |
| NFR-010/011/012 | Idempotence, concurrence, transactions | `CLAUDE.md` (règle 4), ADR-0004 | à venir (US-018, US-022, US-027) |
| NFR-014 | Exactitude monétaire | `shared/money/Money.java`, `CommissionCalculatorTest`, ArchUnit `pas_de_float_ni_double`, ESLint `parseFloat` | en place |
| NFR-015 | Fuseau marocain | `shared/time/BusinessTime.java`, `BusinessTimeTest`, ArchUnit `pas_d_horloge_systeme_directe`, `frontend/src/lib/format.ts` | en place |
| NFR-020 | Performance front | prérendu des pages `[lang]` | en place ; Lighthouse CI à ajouter (US-011) |
| NFR-030 | Sondes de santé | Actuator `health/liveness`, `health/readiness` | en place |
| NFR-040 | Frontières de modules | `ModularityTests`, `ArchitectureRulesTest` | en place |
| NFR-041 | Contrat d'API | `contracts/openapi.yaml`, `npm run check:api` en CI | en place |
| NFR-042 | Qualité de code | Spotless, JaCoCo (seuils), ESLint, TypeScript strict | en place ; SonarQube à brancher |
| NFR-043 | Migrations | Flyway + `ddl-auto: validate` | en place |
| NFR-050 | Pyramide de tests | JUnit, Testcontainers, Vitest, Playwright | en place |
| NFR-051 | Simulateurs | `docker-compose.yml` (Mailpit, WireMock) | en place |
| NFR-060 | Logs structurés | `logging.structured.format.console: ecs` | en place ; traces OpenTelemetry à ajouter |
| NFR-061 | Métriques | Actuator + Prometheus | en place (technique) ; métriques métier avec les US |
| NFR-070 | Accessibilité | axe dans `e2e/i18n.spec.ts` | en place |
| NFR-080/081 | i18n / RTL | dictionnaires + test de clés `config.test.ts`, tests `dir` Playwright | en place |
| NFR-100/101 | Livraison / CI | `.github/workflows/`, Dockerfiles | en place (CI) ; déploiement à définir (D-10) |
| US-038 | Audit append-only | `V2__audit_event.sql` (trigger), `AuditLogIT` | en place |
