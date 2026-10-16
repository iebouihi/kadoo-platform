# Backend Kadoo

Règles générales : voir `../CLAUDE.md`. Spécifique au backend :

- **Java 21, Spring Boot 4** (Spring Framework 7, Jakarta EE 11, Jackson 3). Les noms de starters et certains
  packages de test ont changé par rapport à Boot 3 : vérifier la documentation avant d'ajouter une dépendance.
- **Nouveau module** : package `ma.kadoo.<module>` + `package-info.java` annoté `@ApplicationModule` ;
  API publique dans le package racine, implémentation dans `internal`.
- **Communication entre modules** : appel de l'API publique d'un autre module ou événement applicatif
  (`ApplicationEventPublisher`). Jamais d'accès à ses repositories ou à ses tables.
- **Entités** : `ddl-auto: validate`. Toute colonne passe par une migration `src/main/resources/db/migration/Vn__*.sql`.
- **Contraintes en base** pour l'idempotence et l'unicité (ex. `gift_card.order_id UNIQUE`), avec un test de
  concurrence pour les US de paiement, d'émission et d'utilisation.
- **Erreurs** : `ProblemDetail` (RFC 9457) avec un `code` stable (ex. `CARD_ALREADY_REDEEMED`) déclaré dans
  `contracts/openapi.yaml`.
- **Tests** : critères d'acceptation → tests nommés en français descriptif (`refuse_un_taux_hors_bornes`) ;
  intégration avec `@Import(TestcontainersConfiguration.class)`.

Vérification avant PR : `mvn spotless:apply && mvn verify`.
