# Guide de l'environnement de développement

Ce guide décrit le poste de travail standard de l'équipe Kadoo : outils, IDE, assistant IA et licences.
Objectif : un nouveau développeur est opérationnel en une demi-journée, avec les mêmes garde-fous que le reste de l'équipe.

## 1. Outils de base

| Outil | Version | Rôle | Vérification |
|---|---|---|---|
| Git | récent | Versionnement | `git --version` |
| JDK | **21** (Eclipse Temurin recommandé) | Backend | `java -version` |
| Maven | **3.9+** | Build backend | `mvn -v` |
| Node.js | **22 LTS** (npm inclus) | Frontend | `node -v` |
| Docker + Docker Compose | récent | PostgreSQL, Mailpit, WireMock, Testcontainers | `docker compose version` |
| Claude Code (CLI) | dernière version | Agent de code (voir §4) | `claude --version` |

Windows : utiliser **WSL2** (Ubuntu) pour le dépôt, Docker Desktop avec l'intégration WSL activée.

Premier démarrage :

```bash
git clone https://github.com/iebouihi/kadoo-platform.git && cd kadoo-platform
docker compose up -d
cd frontend && npm ci && npm run dev          # http://localhost:3000
cd ../backend && mvn spring-boot:run -Dspring-boot.run.profiles=local   # http://localhost:8080
```

## 2. IDE retenus

| Usage | IDE | Pourquoi |
|---|---|---|
| **Backend Java / Spring Boot** | **IntelliJ IDEA** (Ultimate de préférence, Community possible) | Meilleur support Spring, JPA, Maven, débogage et refactoring Java |
| **Frontend Next.js / TypeScript** | **VS Code** | Écosystème TypeScript / React, ESLint, Tailwind, Playwright |
| Développeur polyvalent | VS Code pour tout, ou IntelliJ IDEA Ultimate pour tout | Les deux sont acceptés ; les règles de format sont imposées par le build, pas par l'IDE |

Le dépôt fournit les réglages communs :

- `.editorconfig` : encodage, fins de ligne, indentation (respecté par les deux IDE).
- `.vscode/extensions.json` : extensions recommandées, proposées automatiquement à l'ouverture du dossier.
- Format Java imposé par **Spotless** (`mvn spotless:apply`) ; format front par **Prettier** (`npm run format`).

### 2.1 VS Code — extensions

| Extension | Identifiant |
|---|---|
| Claude Code | `anthropic.claude-code` |
| ESLint | `dbaeumer.vscode-eslint` |
| Prettier | `esbenp.prettier-vscode` |
| Tailwind CSS IntelliSense | `bradlc.vscode-tailwindcss` |
| Playwright Test | `ms-playwright.playwright` |
| Vitest | `vitest.explorer` |
| EditorConfig | `editorconfig.editorconfig` |
| YAML (OpenAPI, CI) | `redhat.vscode-yaml` |
| Extension Pack for Java *(si Java dans VS Code)* | `vscjava.vscode-java-pack` |
| Spring Boot Extension Pack *(si Java dans VS Code)* | `vmware.vscode-boot-dev-pack` |

Réglages conseillés (utilisateur) : format à l'enregistrement avec Prettier, correction ESLint à l'enregistrement.

### 2.2 IntelliJ IDEA — configuration

- Projet : ouvrir `backend/pom.xml` comme projet Maven, SDK = JDK 21.
- Plugins : **Claude Code [Beta]** (Marketplace JetBrains), **Spotless Applier** (optionnel), Docker (inclus en Ultimate).
- Tests : lancer `*Test` directement ; les `*IT` nécessitent Docker (Testcontainers).
- Activer « Optimize imports » et « Reformat code » à l'enregistrement uniquement si cela reste compatible avec Spotless (la CI fait foi).

## 3. Accès aux outils de l'équipe

| Outil | Accès | Usage |
|---|---|---|
| GitHub `iebouihi/kadoo-platform` | Invitation par le tech lead | Code, PR, CI |
| Jira `KAN` (imadeddineb.atlassian.net) | Invitation par le PO | Backlog, statuts, sprints |
| Backlog Excel | `docs/backlog/` dans le dépôt | Décisions, NFR, modèle de données |

## 4. Claude Code : installation et licences

Claude Code est l'agent de code de l'équipe (onglet 09 du backlog, ADR-0006). Il existe en trois formes qui partagent
la même configuration (`CLAUDE.md`, `.claude/`, `.mcp.json`) :

- **Extension VS Code** (`anthropic.claude-code`) : panneau intégré, revue des plans, diffs dans l'éditeur, mentions de fichiers.
  Prérequis : VS Code 1.94 ou plus récent. L'extension embarque son propre CLI pour le panneau ; pour taper `claude` dans le
  terminal intégré, installer aussi le CLI.
- **Plugin JetBrains** (IntelliJ IDEA, WebStorm…) : diffs dans le visualiseur de l'IDE, partage de la sélection et des
  diagnostics. Le plugin **n'embarque pas** le CLI : installer d'abord le CLI, puis le plugin, puis redémarrer l'IDE.
- **CLI** : dans n'importe quel terminal (`claude`), indispensable aussi pour la CI et les scripts.

Installation et prise en main : voir la documentation officielle (liens en fin de page).

### 4.1 Licences — décision recommandée

Claude Code fonctionne avec n'importe quel abonnement Claude payant (**Pro, Max, Team, Enterprise**) ou un compte
**Claude Console** (facturation à l'usage via l'API). Aucune clé d'API n'est nécessaire pour se connecter.

| Option | Pour qui | Avantages | Points d'attention |
|---|---|---|---|
| **Claude for Teams** *(recommandé)* | Équipe Kadoo | Facturation centralisée, administration des membres, SSO, réglages Claude Code gérés par l'organisation, accès à Claude Code **et** à Claude web | Vérifier sur la page tarifs que le type de siège choisi inclut Claude Code |
| Claude for Enterprise | Si exigences de conformité fortes | Ajoute capture de domaine, rôles, API de conformité | Contrat commercial |
| Claude Console (API) | Usage ponctuel, CI | Paiement à la consommation, rôle « Claude Code » dédié | Coût variable, à plafonner |
| Pro / Max individuels | **À éviter** pour le projet | — | Comptes personnels : pas de contrôle de l'organisation sur les données ni sur la facturation |

Règles du projet (voir `docs/ai/politique-usage-ia.md`) :

1. Chaque développeur utilise un **siège de l'organisation Kadoo**, jamais un compte personnel, pour le code du projet.
2. L'administrateur restreint la connexion à l'organisation via les **réglages gérés** (`forceLoginMethod`, `forceLoginOrgUUID`),
   déployés sur les postes.
3. Le CI utilise un jeton dédié stocké dans les secrets GitHub (jamais dans le dépôt).

### 4.2 Connexion et vérification

```bash
claude            # première exécution : connexion dans le navigateur avec le compte de l'organisation
/status           # vérifier compte, organisation, modèle et serveurs MCP
```

Dans VS Code : icône Claude Code (étincelle) → se connecter ; puis `/status` dans le panneau.

### 4.3 Connexion à Jira (MCP Atlassian)

Le dépôt déclare le serveur MCP Atlassian dans `.mcp.json` : il est proposé automatiquement à l'ouverture du projet.

```bash
claude            # accepter le serveur « atlassian » proposé par le projet
/mcp              # s'authentifier (OAuth Atlassian, compte invité sur le projet KAN)
```

Une fois connecté, l'agent lit les stories (`KAN-xx`), leurs critères d'acceptation, et peut commenter ou changer un statut
(voir les règles dans `docs/ai/politique-usage-ia.md`).

### 4.4 Garde-fous déjà configurés dans le dépôt

| Fichier | Effet |
|---|---|
| `CLAUDE.md` (racine, `backend/`, `frontend/`) | Contexte et règles non négociables lus à chaque session |
| `.claude/commands/*.md` | Commandes `/implement-us`, `/tests-from-ac`, `/security-review`, `/rtl-check` |
| `.claude/settings.json` | Interdit la lecture des fichiers `.env`, clés et certificats ; interdit `git push --force` |
| `.mcp.json` | Serveur MCP Atlassian (Jira) partagé par l'équipe |

Réglages personnels : `.claude/settings.local.json` (ignoré par Git).

## 5. Checklist d'arrivée

- [ ] Outils de base installés et vérifiés (§1)
- [ ] IDE configuré avec les extensions / plugins (§2)
- [ ] Accès GitHub et Jira reçus (§3)
- [ ] Siège Claude de l'organisation attribué, connexion vérifiée avec `/status` (§4)
- [ ] MCP Atlassian authentifié (`/mcp`) et lecture d'une story testée
- [ ] `docker compose up -d`, puis `mvn verify` et `npm test` verts en local
- [ ] Politique d'usage de l'IA lue et acceptée (`docs/ai/politique-usage-ia.md`)

## Sources

- [Claude Code dans VS Code](https://code.claude.com/docs/en/vs-code)
- [Claude Code dans les IDE JetBrains](https://code.claude.com/docs/en/jetbrains)
- [Authentification Claude Code (Pro, Max, Team, Enterprise, Console)](https://code.claude.com/docs/en/authentication)
- [Tarifs Claude](https://claude.com/pricing)
- [Atlassian Rovo MCP Server — démarrage](https://support.atlassian.com/atlassian-rovo-mcp-server/docs/getting-started-with-the-atlassian-remote-mcp-server/)
