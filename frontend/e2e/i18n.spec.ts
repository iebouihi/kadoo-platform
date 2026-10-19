import { expect, test } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";

test.describe("Langues et RTL (KAN-67, KAN-73)", () => {
  test("la racine redirige vers la langue du navigateur", async ({ browser }) => {
    const context = await browser.newContext({ locale: "ar-MA" });
    const page = await context.newPage();
    await page.goto("/");
    await expect(page).toHaveURL(/\/ar$/);
    await context.close();
  });

  test("l'arabe s'affiche de droite à gauche", async ({ page }) => {
    await page.goto("/ar");
    await expect(page.locator("html")).toHaveAttribute("dir", "rtl");
    await expect(page.locator("html")).toHaveAttribute("lang", "ar-MA");
  });

  test("le français s'affiche de gauche à droite", async ({ page }) => {
    await page.goto("/fr");
    await expect(page.locator("html")).toHaveAttribute("dir", "ltr");
  });

  test("le choix explicite de langue est prioritaire", async ({ page, context }) => {
    await context.addCookies([{ name: "KADOO_LOCALE", value: "en", url: page.url() || "http://localhost:3000" }]);
    await page.goto("/");
    await expect(page).toHaveURL(/\/en$/);
  });
});

test.describe("Accessibilité (NFR-070)", () => {
  for (const lang of ["fr", "ar", "en"]) {
    test(`aucune violation WCAG 2.1 AA sur /${lang}`, async ({ page }) => {
      await page.goto(`/${lang}`);
      const results = await new AxeBuilder({ page }).withTags(["wcag2a", "wcag2aa", "wcag21a", "wcag21aa"]).analyze();
      expect(results.violations).toEqual([]);
    });
  }
});

test("en-têtes de sécurité présents (NFR-001)", async ({ request }) => {
  const res = await request.get("/fr");
  expect(res.headers()["x-content-type-options"]).toBe("nosniff");
  expect(res.headers()["content-security-policy"]).toContain("frame-ancestors 'none'");
  expect(res.headers()["x-powered-by"]).toBeUndefined();
});
