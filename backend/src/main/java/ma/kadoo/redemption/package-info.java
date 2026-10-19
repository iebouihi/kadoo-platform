/**
 * Module redemption — Vérification et utilisation des cartes, expiration, anti-force brute, blocage.
 *
 * <p>User stories : KAN-49, KAN-50, KAN-51, KAN-52, KAN-53, KAN-54. À implémenter selon le flux décrit dans CLAUDE.md.
 * Les classes publiques de ce package forment l'API du module ; tout le reste va dans des sous-packages
 * (ex. redemption.internal), invisibles des autres modules (vérifié par ModularityTests).
 */
@org.springframework.modulith.ApplicationModule(displayName = "redemption")
package ma.kadoo.redemption;
