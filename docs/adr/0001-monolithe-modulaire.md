# ADR-0001 — Backend en monolithe modulaire Spring Boot

- Statut : acceptée (Sprint 0)
- Date : 2026-10-16

## Contexte

Équipe réduite, maîtrisant Java. Le flux critique paiement → commande → transaction financière → carte doit être
atomique et idempotent (NFR-010, NFR-012). Le MVP doit être simple à exploiter.

## Décision

Un seul déployable Java 21 / Spring Boot 4, découpé en modules métier (`identity`, `partner`, `catalog`, `order`,
`payment`, `issuance`, `redemption`, `notification`, `reporting`, `audit`, `i18n`) sous `ma.kadoo`.
Les frontières sont vérifiées à chaque build par Spring Modulith (`ModularityTests`) et ArchUnit. Une seule base
PostgreSQL ; chaque module possède ses tables.

## Conséquences

- Transactions ACID simples sur le flux financier ; un seul pipeline et un seul service à surveiller.
- Un module pourra être extrait en service si la charge l'exige, sans réécriture (frontières déjà propres).
- Discipline nécessaire : aucune jointure ni accès direct aux tables d'un autre module.

## Alternatives écartées

Microservices : coût d'exploitation et cohérence distribuée (sagas) disproportionnés pour le MVP.
