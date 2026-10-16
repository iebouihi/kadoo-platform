# ADR-0004 — Traitements asynchrones par outbox transactionnelle

- Statut : proposée (à appliquer avec US-018 / US-022)
- Date : 2026-10-16

## Décision

Pas de broker de messages au MVP. Les effets différés (émission de carte, envoi d'email, rapprochement PSP,
expirations) sont publiés comme événements applicatifs enregistrés dans la même transaction que l'action métier
(registre d'événements Spring Modulith, `spring-modulith-starter-jpa`), puis traités par des workers. Chaque
traitement est idempotent grâce à une contrainte d'unicité en base.

## Conséquences

- Aucun email envoyé ni carte émise pour une transaction annulée (NFR-012).
- Reprise automatique après panne d'un service tiers (NFR-013, NFR-032).
- La dépendance et sa migration Flyway sont ajoutées avec la première US asynchrone (elles ne sont pas encore dans le pom).
