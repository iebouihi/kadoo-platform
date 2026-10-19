/**
 * Module payment — Intégration PSP, notifications signées, rapprochement, remboursements, transactions financières, reversements.
 *
 * <p>User stories : KAN-38, KAN-39, KAN-40, KAN-41, KAN-42, KAN-43. À implémenter selon le flux décrit dans CLAUDE.md.
 * Les classes publiques de ce package forment l'API du module ; tout le reste va dans des sous-packages
 * (ex. payment.internal), invisibles des autres modules (vérifié par ModularityTests).
 */
@org.springframework.modulith.ApplicationModule(displayName = "payment")
package ma.kadoo.payment;
