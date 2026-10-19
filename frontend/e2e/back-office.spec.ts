import { expect, test } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";

/**
 * Back-office (KAN-17) sans session : redirections, RTL, CSP, accessibilité. Les parcours de connexion réussie et
 * refusée, avec le vrai backend, sont dans login.spec.ts.
 */
test.describe("Connexion back-office (KAN-17)", () => {
  test("AC5 — sans session, le tableau de bord renvoie vers la connexion", async ({ page }) => {
    await page.goto("/ar/admin");
    await expect(page).toHaveURL(/\/ar\/login$/);
    await page.goto("/en/partner");
    await expect(page).toHaveURL(/\/en\/login$/);
  });

  test("la page de connexion s'affiche de droite à gauche en arabe", async ({ page }) => {
    await page.goto("/ar/login");
    await expect(page.locator("html")).toHaveAttribute("dir", "rtl");
    await expect(page.getByRole("heading", { level: 1 })).toHaveText("تسجيل الدخول إلى فضاء Kadoo");
  });

  test("CSP stricte avec nonce sur le back-office (NFR-001, ADR-0005)", async ({ page }) => {
    const response = await page.goto("/fr/login");
    const csp = response?.headers()["content-security-policy"] ?? "";
    const nonce = /'nonce-([^']+)'/.exec(csp)?.[1];
    expect(nonce).toBeTruthy();
    expect(csp).toContain("'strict-dynamic'");
    // Chaque script de la page porte le nonce de la requête, sinon le navigateur le bloquerait.
    const scripts = page.locator("script");
    const count = await scripts.count();
    expect(count).toBeGreaterThan(0);
    for (let i = 0; i < count; i++) {
      expect(await scripts.nth(i).evaluate((s) => (s as HTMLScriptElement).nonce)).toBe(nonce);
    }
    // Le formulaire est interactif : le JavaScript n'a pas été bloqué.
    await expect(page.getByRole("button", { name: "Se connecter" })).toBeEnabled();
  });
});

test.describe("Accessibilité de la connexion (NFR-070)", () => {
  for (const lang of ["fr", "ar", "en"]) {
    test(`aucune violation WCAG 2.1 AA sur /${lang}/login`, async ({ page }) => {
      await page.goto(`/${lang}/login`);
      const results = await new AxeBuilder({ page }).withTags(["wcag2a", "wcag2aa", "wcag21a", "wcag21aa"]).analyze();
      expect(results.violations).toEqual([]);
    });
  }
});
