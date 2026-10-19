import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { LOCALE_COOKIE, type Locale, locales, resolveLocale } from "@/i18n/config";
import { SESSION_COOKIE, loginPath } from "@/lib/backoffice/auth";
import { backOfficeCsp } from "@/lib/backoffice/csp";

const BACK_OFFICE = /^\/(fr|ar|en)\/(login|admin|partner)(\/|$)/;

/**
 * 1. Redirige toute URL sans préfixe de langue vers /fr, /ar ou /en (KAN-67).
 * 2. Back-office (KAN-17) : sans cookie de session, renvoie vers la connexion (NFR-002), et applique la CSP stricte
 *    avec nonce (ADR-0005). Ce n'est pas un contrôle d'accès : la sécurité est assurée par le backend (NFR-003),
 *    qui vérifie la session à chaque appel.
 */
export function proxy(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const hasLocale = locales.some((l) => pathname === `/${l}` || pathname.startsWith(`/${l}/`));
  if (!hasLocale) {
    const locale = resolveLocale(request.cookies.get(LOCALE_COOKIE)?.value, request.headers.get("accept-language"));
    request.nextUrl.pathname = `/${locale}${pathname === "/" ? "" : pathname}`;
    return NextResponse.redirect(request.nextUrl);
  }

  const backOffice = BACK_OFFICE.exec(pathname);
  if (!backOffice) return;
  const [, locale, area] = backOffice;

  if (area !== "login" && !request.cookies.has(SESSION_COOKIE)) {
    return NextResponse.redirect(new URL(loginPath(locale as Locale), request.url));
  }

  const nonce = Buffer.from(crypto.randomUUID()).toString("base64");
  const csp = backOfficeCsp(nonce, process.env.NODE_ENV === "development");
  const requestHeaders = new Headers(request.headers);
  requestHeaders.set("x-nonce", nonce);
  requestHeaders.set("Content-Security-Policy", csp);
  const response = NextResponse.next({ request: { headers: requestHeaders } });
  response.headers.set("Content-Security-Policy", csp);
  return response;
}

export const config = {
  matcher: [
    {
      source: "/((?!_next|api|favicon.ico|robots.txt|sitemap.xml|.*\\..*).*)",
      missing: [
        { type: "header", key: "next-router-prefetch" },
        { type: "header", key: "purpose", value: "prefetch" },
      ],
    },
  ],
};
