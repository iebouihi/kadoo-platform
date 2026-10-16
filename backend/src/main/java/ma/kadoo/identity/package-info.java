/**
 * Module identity — Comptes back-office, authentification, invitations, MFA.
 *
 * <p>User stories : US-001, US-002, US-003, US-047. À implémenter selon le flux décrit dans CLAUDE.md.
 * Les classes publiques de ce package forment l'API du module ; tout le reste va dans des sous-packages
 * (ex. identity.internal), invisibles des autres modules (vérifié par ModularityTests).
 */
@org.springframework.modulith.ApplicationModule(displayName = "identity")
package ma.kadoo.identity;
