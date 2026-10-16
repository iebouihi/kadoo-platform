import { describe, expect, it } from "vitest";
import { formatDateTime, formatMoney } from "./format";

/** Intl utilise des espaces insécables : on les normalise pour comparer. */
const plain = (s: string) => s.replace(/[\u00a0\u202f]/g, " ");

describe("formatMoney (NFR-014)", () => {
  it("affiche toujours deux décimales en dirhams", () => {
    // Formats CLDR de fr-MA et en (le séparateur de milliers de fr-MA est le point)
    expect(plain(formatMoney("1250", "fr"))).toBe("1.250,00 MAD");
    expect(plain(formatMoney("1250.5", "en"))).toBe("MAD 1,250.50");
  });
  it("utilise des chiffres occidentaux en arabe (usage marocain)", () => {
    expect(formatMoney("99.90", "ar")).toMatch(/99[.,]90/);
  });
  it("refuse un montant qui n'est pas une chaîne décimale à 2 chiffres max", () => {
    expect(() => formatMoney("12.345", "fr")).toThrow();
    expect(() => formatMoney("abc", "fr")).toThrow();
  });
});

describe("formatDateTime (NFR-015)", () => {
  it("affiche l'heure du Maroc quel que soit le fuseau du navigateur", () => {
    // 22:59:59 UTC = 23:59:59 à Casablanca en heure normale (UTC+1)
    expect(formatDateTime("2027-10-16T22:59:59Z", "fr")).toContain("23:59");
  });
});
