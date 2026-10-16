/**
 * Module redemption — Vérification et utilisation des cartes, expiration, anti-force brute, blocage.
 *
 * <p>User stories : US-026 à US-029, US-051, US-052. À implémenter selon le flux décrit dans CLAUDE.md.
 * Les classes publiques de ce package forment l'API du module ; tout le reste va dans des sous-packages
 * (ex. redemption.internal), invisibles des autres modules (vérifié par ModularityTests).
 */
@org.springframework.modulith.ApplicationModule(displayName = "redemption")
package ma.koadooo.redemption;
