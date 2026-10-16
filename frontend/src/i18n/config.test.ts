import { describe, expect, it } from "vitest";
import { resolveLocale } from "./config";
import fr from "@/app/[lang]/dictionaries/fr.json";
import ar from "@/app/[lang]/dictionaries/ar.json";
import en from "@/app/[lang]/dictionaries/en.json";

function keys(obj: object, prefix = ""): string[] {
  return Object.entries(obj).flatMap(([k, v]) =>
    v && typeof v === "object" ? keys(v, `${prefix}${k}.`) : [`${prefix}${k}`],
  );
}

describe("resolveLocale (US-040)", () => {
  it("donne la priorité au choix explicite (cookie) sur le navigateur", () => {
    expect(resolveLocale("ar", "en-US,en;q=0.9")).toBe("ar");
  });
  it("utilise la langue du navigateur en respectant les poids q", () => {
    expect(resolveLocale(undefined, "de;q=0.9,en;q=0.8,ar;q=0.95")).toBe("ar");
  });
  it("accepte les variantes régionales (fr-MA → fr)", () => {
    expect(resolveLocale(undefined, "fr-MA")).toBe("fr");
  });
  it("se replie sur le français (US-045)", () => {
    expect(resolveLocale("xx", "de-DE")).toBe("fr");
    expect(resolveLocale(undefined, null)).toBe("fr");
  });
});

describe("catalogues de traduction (NFR-080)", () => {
  it("ar et en ont exactement les mêmes clés que fr", () => {
    expect(keys(ar).sort()).toEqual(keys(fr).sort());
    expect(keys(en).sort()).toEqual(keys(fr).sort());
  });
});
