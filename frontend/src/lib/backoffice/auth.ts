import type { Locale } from "@/i18n/config";
import type { components } from "@/lib/api/schema";

/**
 * Back-office (KAN-17) : helpers partagés client / serveur. Aucune règle d'accès ici : la sécurité est assurée
 * par le backend (RG6, NFR-003) ; le frontend ne fait que rediriger pour l'ergonomie.
 */
export type Role = components["schemas"]["Role"];
export type CurrentUser = components["schemas"]["CurrentUser"];

/** Cookie de session posé par le backend (HttpOnly : jamais lu par le JavaScript du navigateur). */
export const SESSION_COOKIE = "SESSION";
export const CSRF_COOKIE = "XSRF-TOKEN";
export const CSRF_HEADER = "X-XSRF-TOKEN";

/** AC1 : chaque rôle arrive sur son tableau de bord. */
export function dashboardPath(locale: Locale, role: Role): string {
  return `/${locale}/${role === "ADMIN" ? "admin" : "partner"}`;
}

export function loginPath(locale: Locale): string {
  return `/${locale}/login`;
}

export function readCookie(cookieHeader: string, name: string): string | undefined {
  for (const part of cookieHeader.split(";")) {
    const [key, ...value] = part.trim().split("=");
    if (key === name) return decodeURIComponent(value.join("="));
  }
  return undefined;
}

/**
 * Mutation vers l'API (même origine, via la réécriture /api/v1 de next.config.ts) avec le jeton CSRF (NFR-002).
 * Le jeton est lu dans le cookie XSRF-TOKEN, déposé par le backend ; il n'est jamais stocké ailleurs.
 */
export async function postWithCsrf(path: string, body?: unknown): Promise<Response> {
  if (!readCookie(document.cookie, CSRF_COOKIE)) {
    await fetch("/api/v1/auth/csrf", { credentials: "same-origin" });
  }
  return fetch(path, {
    method: "POST",
    credentials: "same-origin",
    headers: {
      "Content-Type": "application/json",
      [CSRF_HEADER]: readCookie(document.cookie, CSRF_COOKIE) ?? "",
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
}
