# Politique d'usage de l'IA — projet Kadoo

À lire et accepter par chaque membre de l'équipe avant sa première PR. Référence : onglet 09 du backlog, ADR-0006.

## 1. Principe

L'IA propose, l'humain décide. Chaque ligne fusionnée a un auteur humain qui l'a lue, comprise et qui en répond.

## 2. Outils autorisés

Uniquement les outils validés par le responsable sécurité / DPO, utilisés avec un compte professionnel dont le
contrat exclut la réutilisation des données pour l'entraînement. Liste tenue à jour par l'architecte.

| Outil | Usage | Compte |
|---|---|---|
| Claude Code (extension VS Code, plugin JetBrains, CLI) | Agent de code : plan, tests, implémentation, revue | Siège de l'organisation Kadoo (Claude for Teams ou Enterprise) |
| Claude (web / desktop) | Analyse, rédaction, revue de spécifications | Même siège |
| Connecteur MCP Atlassian | Lecture et mise à jour des stories Jira `KAN` | Compte Atlassian nominatif de chaque membre |

Installation et licences : `docs/dev/environnement-dev.md`. Les comptes personnels (Pro / Max individuels) ne sont pas
utilisés pour le code du projet.

## 3. Ce qui ne doit jamais être envoyé à un outil IA

- Données de production, données personnelles réelles (acheteurs, bénéficiaires, gestionnaires).
- Secrets : mots de passe, clés API, clés HMAC, identifiants PSP, certificats, fichiers `.env`.
- RIB, ICE réels, contrats partenaires, données financières réelles.
- Journaux de production non masqués.

## 4. Ce que l'IA peut faire

Rédiger ou réviser des spécifications, proposer des options d'architecture, générer du code et des tests à partir
d'une US prête, relire des PR, trier des alertes de sécurité, rédiger de la documentation, analyser des échecs de CI.

## 5. Ce que l'IA ne fait jamais

- Approuver ou fusionner une PR, déployer en production, accéder à la production.
- Clôturer une alerte de sécurité.
- Trancher une décision métier ouverte (onglet 06) ou inventer une règle de gestion.
- Écrire de la cryptographie maison.

## 6. Zones sensibles

Paiement, émission, utilisation, commission, authentification, migrations, configuration de sécurité :
revue par un senior et un second relecteur, tests de concurrence et de sécurité obligatoires.

## 7. Jira comme source d'exécution (MCP Atlassian)

Le backlog est chargé dans Jira, projet **Kadoo** (`KAN`) : 13 epics (`KAN-4` à `KAN-16`) et 60 stories (`KAN-17` à
`KAN-76`), avec description, règles de gestion, critères d'acceptation, dépendances et décisions liées.
Correspondance complète `US-xxx` ↔ `KAN-yy` : `docs/backlog/jira-mapping.md`.

**Répartition des rôles**

| Source | Fait foi pour |
|---|---|
| Jira `KAN` | Statut, priorité, sprint, assignation, critères d'acceptation **en vigueur** (le PO les modifie dans Jira) |
| `docs/backlog/Kadoo_Backlog_MVP_v1.2.xlsx` | Décisions (onglet 06), modèle de données (05), cycles de vie (04), NFR (07) |
| Dépôt Git | Code, tests, contrat d'API, ADR |

**Ce que l'agent fait via le MCP Atlassian**

- Lire la story (`KAN-xx` ou libellé `US-xxx`) **avant** tout plan ; si les critères diffèrent du fichier Excel, Jira
  fait foi et l'écart est signalé au PO.
- Commenter la story : plan validé, lien de la PR, hypothèses retenues, questions ouvertes.
- Faire passer la story de « To Do » à « In Progress » au démarrage, puis « In Review » à l'ouverture de la PR,
  **uniquement à la demande du développeur**.

**Ce que l'agent ne fait jamais dans Jira**

- Passer une story à « Done » : c'est le PO, après recette (Definition of Done).
- Créer, supprimer, découper ou re-prioriser des stories, ou modifier leurs critères d'acceptation.
- Copier dans Jira des données personnelles, des secrets ou des extraits de logs non masqués.

**Configuration** : le serveur MCP est déclaré dans `.mcp.json` à la racine du dépôt ; chaque développeur s'authentifie
avec son propre compte Atlassian (`/mcp` dans Claude Code). Les droits de l'agent sont donc exactement ceux du développeur.

## 8. Traçabilité

- Branche `feat/US-xxx-description`, commits `US-xxx: …`, PR titrée `US-xxx (KAN-yy) — …` avec le lien Jira.
- La PR indique si l'IA a été utilisée et sur quelles parties.
- Les commandes et fichiers de contexte IA sont versionnés (`CLAUDE.md`, `.claude/`, `.mcp.json`) et revus comme du code.

## 9. Licences

- **Logiciel** : toute dépendance proposée par l'IA est vérifiée (licence compatible, projet maintenu) avant ajout.
- **Outils IA** : sièges attribués et retirés par l'administrateur de l'organisation Claude ; connexion restreinte à
  l'organisation par les réglages gérés (voir `docs/dev/environnement-dev.md`, §4).

## 10. Mesure

Indicateurs suivis à chaque sprint : délai par US, défauts échappés, taux de PR fortement reprises, incidents de
sécurité liés au code assisté (objectif : 0). Ajustements décidés en rétrospective.
