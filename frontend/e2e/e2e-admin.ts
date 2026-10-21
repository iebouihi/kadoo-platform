/**
 * Compte Admin synthétique des tests e2e, créé au démarrage du backend par l'amorçage du premier Admin
 * (KADOO_BOOTSTRAP_ADMIN_*, KAN-17) quand la base n'a encore aucun Admin. Aucune donnée réelle (NFR-004).
 */
export const E2E_ADMIN = {
  email: process.env.E2E_ADMIN_EMAIL ?? "admin-e2e@kadoo.test",
  password: process.env.E2E_ADMIN_PASSWORD ?? "Synthetique-E2E-2026!",
};
