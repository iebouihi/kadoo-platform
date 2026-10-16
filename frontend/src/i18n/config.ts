/**
 * Langues de l'interface (US-039, US-040, US-045, US-046).
 * Le segment d'URL est court (/fr, /ar, /en) ; le code métier est la locale BCP 47 utilisée par le backend.
 */
export const locales = ["fr", "ar", "en"] as const;
export type Locale = (typeof locales)[number];

export const defaultLocale: Locale = "fr";

/** Cookie posé quand l'utilisateur choisit explicitement une langue (prioritaire, US-040). */
export const LOCALE_COOKIE = "KADOO_LOCALE";

export const bcp47: Record<Locale, string> = {
  fr: "fr-MA",
  ar: "ar-MA",
  en: "en",
};

export const direction: Record<Locale, "ltr" | "rtl"> = {
  fr: "ltr",
  ar: "rtl",
  en: "ltr",
};

/** Fuseau de référence pour l'affichage, les filtres de période et l'expiration (NFR-015). */
export const BUSINESS_TIME_ZONE = "Africa/Casablanca";

export function isLocale(value: string | undefined | null): value is Locale {
  return value != null && (locales as readonly string[]).includes(value);
}

/**
 * Ordre de résolution (US-040) : choix explicite (cookie) > langue du navigateur > fr.
 * Le préfixe d'URL, quand il existe, est traité avant l'appel (il gagne toujours).
 */
export function resolveLocale(cookieValue: string | undefined, acceptLanguage: string | null): Locale {
  if (isLocale(cookieValue)) return cookieValue;
  if (acceptLanguage) {
    const ranked = acceptLanguage
      .split(",")
      .map((part) => {
        const [tag = "", ...params] = part.trim().split(";");
        const q = params.find((p) => p.trim().startsWith("q="));
        return { tag: tag.toLowerCase(), q: q ? Number(q.trim().slice(2)) : 1 };
      })
      .filter((entry) => entry.tag && !Number.isNaN(entry.q))
      .sort((a, b) => b.q - a.q);
    for (const { tag } of ranked) {
      const primary = tag.split("-")[0];
      if (isLocale(primary)) return primary;
    }
  }
  return defaultLocale;
}
