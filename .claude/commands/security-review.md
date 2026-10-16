---
description: Revue de sécurité ciblée des changements en cours (complète, ne remplace pas la revue humaine)
---

Fais une revue de sécurité des changements par rapport à `main` (`git diff main...HEAD`). Pour chaque point,
indique le fichier, la ligne, la gravité (bloquant / majeur / mineur) et la correction proposée.

Vérifie en priorité :

1. **Isolation partenaire** (NFR-003) : tout accès back-office filtré par le `partnerId` de la session ; test d'accès croisé présent.
2. **Paiement** (NFR-005) : signature PSP vérifiée avant tout traitement, anti-rejeu, montant comparé, idempotence par contrainte unique.
3. **Codes et jetons** (NFR-004) : génération par `SecureRandom`, stockage HMAC/hash, jamais en clair dans logs / audit / réponses.
4. **Concurrence** (NFR-010/011) : mise à jour conditionnelle + contrainte unique pour l'utilisation et l'émission.
5. **Entrées** (NFR-008) : validation Bean Validation, aucune concaténation SQL, aucun HTML non échappé.
6. **Limitation de débit** (NFR-006) sur connexion, reset, vérification de code.
7. **Données personnelles** (NFR-090) : minimisation, masquage dans les logs et exports partenaire.
8. **Dépendances ajoutées** : utilité, maintenance, licence.

Ne clos aucun point toi-même : la décision revient au responsable sécurité.
