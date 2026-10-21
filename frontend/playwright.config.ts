import { defineConfig, devices } from "@playwright/test";
import { E2E_ADMIN } from "./e2e/e2e-admin";

const PORT = Number(process.env.PORT ?? 3000);
const API_URL = process.env.KADOO_API_URL ?? "http://localhost:8080";

export default defineConfig({
  testDir: "./e2e",
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? [["html", { open: "never" }], ["github"]] : "list",
  use: { baseURL: `http://localhost:${PORT}`, trace: "retain-on-failure" },
  // Mobile d'abord (NFR-071, NFR-072)
  projects: [
    { name: "mobile-chrome", use: { ...devices["Pixel 7"] } },
    { name: "desktop-chrome", use: { ...devices["Desktop Chrome"] } },
  ],
  webServer: [
    {
      // Vrai backend pour les parcours de connexion (login.spec.ts). Prérequis : PostgreSQL (docker compose up -d)
      // et Java 21. Un backend déjà lancé est réutilisé en local : il doit avoir été démarré avec ces variables.
      command: `mvn -q -f ../backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--server.port=${new URL(API_URL).port}`,
      url: `${API_URL}/actuator/health`,
      reuseExistingServer: !process.env.CI,
      timeout: 300_000,
      env: {
        KADOO_BOOTSTRAP_ADMIN_EMAIL: E2E_ADMIN.email,
        KADOO_BOOTSTRAP_ADMIN_PASSWORD: E2E_ADMIN.password,
      },
    },
    {
      command: `npm run start -- -p ${PORT}`,
      url: `http://localhost:${PORT}/fr`,
      reuseExistingServer: !process.env.CI,
      timeout: 120_000,
    },
  ],
});
