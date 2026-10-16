# Politique d'usage de l'IA — projet KOADOOO

À lire et accepter par chaque membre de l'équipe avant sa première PR. Référence : onglet 09 du backlog, ADR-0006.

## 1. Principe

L'IA propose, l'humain décide. Chaque ligne fusionnée a un auteur humain qui l'a lue, comprise et qui en répond.

## 2. Outils autorisés

Uniquement les outils validés par le responsable sécurité / DPO, utilisés avec un compte professionnel dont le
contrat exclut la réutilisation des données pour l'entraînement. Liste tenue à jour par l'architecte.

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

## 7. Traçabilité

- Branche `feat/US-xxx-…`, commits `US-xxx: …`.
- La PR indique si l'IA a été utilisée et sur quelles parties.
- Les commandes et fichiers de contexte IA sont versionnés (`CLAUDE.md`, `.claude/commands/`) et revus comme du code.

## 8. Licences

Toute dépendance proposée par l'IA est vérifiée (licence compatible, projet maintenu) avant ajout.

## 9. Mesure

Indicateurs suivis à chaque sprint : délai par US, défauts échappés, taux de PR fortement reprises, incidents de
sécurité liés au code assisté (objectif : 0). Ajustements décidés en rétrospective.
