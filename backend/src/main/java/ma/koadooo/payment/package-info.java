/**
 * Module payment — Intégration PSP, notifications signées, rapprochement, remboursements, transactions financières, reversements.
 *
 * <p>User stories : US-018 à US-021, US-049, US-050. À implémenter selon le flux décrit dans CLAUDE.md.
 * Les classes publiques de ce package forment l'API du module ; tout le reste va dans des sous-packages
 * (ex. payment.internal), invisibles des autres modules (vérifié par ModularityTests).
 */
@org.springframework.modulith.ApplicationModule(displayName = "payment")
package ma.koadooo.payment;
