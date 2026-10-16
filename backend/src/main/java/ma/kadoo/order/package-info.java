/**
 * Module order — Commande sans compte, bénéficiaire, personnalisation, expiration des commandes.
 *
 * <p>User stories : US-014 à US-017, US-048. À implémenter selon le flux décrit dans CLAUDE.md.
 * Les classes publiques de ce package forment l'API du module ; tout le reste va dans des sous-packages
 * (ex. order.internal), invisibles des autres modules (vérifié par ModularityTests).
 */
@org.springframework.modulith.ApplicationModule(displayName = "order")
package ma.kadoo.order;
