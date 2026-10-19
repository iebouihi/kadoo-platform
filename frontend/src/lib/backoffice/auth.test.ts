import { describe, expect, it } from "vitest";
import { dashboardPath, loginPath, readCookie } from "./auth";

describe("dashboardPath (KAN-17 AC1)", () => {
  it("envoie l'Admin sur son tableau de bord", () => {
    expect(dashboardPath("fr", "ADMIN")).toBe("/fr/admin");
  });
  it("envoie le gestionnaire sur le tableau de bord partenaire, dans sa langue", () => {
    expect(dashboardPath("ar", "PARTNER_MANAGER")).toBe("/ar/partner");
  });
});

describe("loginPath (KAN-17 AC5)", () => {
  it("garde la langue courante", () => {
    expect(loginPath("en")).toBe("/en/login");
  });
});

describe("readCookie", () => {
  it("lit un cookie parmi d'autres", () => {
    expect(readCookie("KADOO_LOCALE=fr; XSRF-TOKEN=abc%3D%3D; other=1", "XSRF-TOKEN")).toBe("abc==");
  });
  it("renvoie undefined si le cookie est absent", () => {
    expect(readCookie("KADOO_LOCALE=fr", "XSRF-TOKEN")).toBeUndefined();
    expect(readCookie("", "XSRF-TOKEN")).toBeUndefined();
  });
});
