import { BUSINESS_TIME_ZONE, bcp47, type Locale } from "@/i18n/config";

/**
 * Les montants circulent en chaîne décimale ("1250.00") et ne sont jamais recalculés côté client (NFR-014).
 * Number() n'est utilisé ici que pour l'affichage, jamais pour un calcul métier.
 * Chiffres occidentaux en arabe : usage marocain (KAN-73).
 */
export function formatMoney(amount: string, locale: Locale, currency = "MAD"): string {
  if (!/^-?\d+(\.\d{1,2})?$/.test(amount)) {
    throw new Error(`Montant invalide : ${amount}`);
  }
  return new Intl.NumberFormat(bcp47[locale], {
    style: "currency",
    currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
    numberingSystem: "latn",
  }).format(Number(amount));
}

/** Dates affichées dans le fuseau métier, quel que soit le fuseau du navigateur (NFR-015). */
export function formatDateTime(isoUtc: string, locale: Locale): string {
  return new Intl.DateTimeFormat(bcp47[locale], {
    dateStyle: "long",
    timeStyle: "short",
    timeZone: BUSINESS_TIME_ZONE,
    numberingSystem: "latn",
  }).format(new Date(isoUtc));
}
