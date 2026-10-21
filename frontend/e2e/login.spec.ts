import { expect, test } from "@playwright/test";
import { E2E_ADMIN } from "./e2e-admin";

/**
 * Connexion back-office de bout en bout (KAN-17) : navigateur → Next.js → vrai backend → PostgreSQL. Aucun appel
 * simulé : la session, le jeton CSRF et la vérification du rôle côté serveur sont les vrais.
 */
test.describe("Connexion back-office de bout en bout (KAN-17)", () => {
  test("AC1 — identifiants valides : l'Admin arrive sur son tableau de bord", async ({ page }) => {
    await page.goto("/fr/login");
    await page.getByLabel("Adresse email").fill(E2E_ADMIN.email);
    await page.getByLabel("Mot de passe").fill(E2E_ADMIN.password);
    await page.getByRole("button", { name: "Se connecter" }).click();

    await expect(page).toHaveURL(/\/fr\/admin$/);
    await expect(page.getByRole("heading", { level: 1 })).toHaveText("Tableau de bord Admin");
    await expect(page.getByText(E2E_ADMIN.email)).toBeVisible();
  });

  test("AC2 — identifiants refusés : message générique, on reste sur la connexion", async ({ page }) => {
    await page.goto("/fr/login");
    // Email sans compte : un mauvais mot de passe sur le compte e2e finirait par le verrouiller (5 échecs).
    await page.getByLabel("Adresse email").fill("inconnu-e2e@kadoo.test");
    await page.getByLabel("Mot de passe").fill("pas-le-bon-mot-de-passe");
    await page.getByRole("button", { name: "Se connecter" }).click();

    // Limité au formulaire : l'annonceur de navigation de Next.js porte aussi role="alert".
    await expect(page.locator("form").getByRole("alert")).toHaveText("Email ou mot de passe incorrect.");
    await expect(page).toHaveURL(/\/fr\/login$/);
  });
});
