import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import type { Locale } from "@/i18n/config";
import { type CurrentUser, type Role, SESSION_COOKIE, dashboardPath, loginPath } from "./auth";

/** Adresse du backend vue depuis le serveur Next.js (jamais exposée au navigateur, NFR-004). */
const API_URL = process.env.KADOO_API_URL ?? "http://localhost:8080";

/**
 * Utilisateur de la session, demandé au backend à chaque requête (aucun cache : une session expirée ou révoquée
 * doit être vue immédiatement, AC5). Renvoie null sans session valide.
 */
export async function getCurrentUser(): Promise<CurrentUser | null> {
  const session = (await cookies()).get(SESSION_COOKIE)?.value;
  if (!session) return null;
  const response = await fetch(`${API_URL}/api/v1/auth/me`, {
    headers: { cookie: `${SESSION_COOKIE}=${session}` },
    cache: "no-store",
  });
  if (response.status === 401) return null;
  if (!response.ok) throw new Error(`GET /auth/me : statut ${response.status}`);
  return (await response.json()) as CurrentUser;
}

/**
 * Exige une session du rôle attendu : sans session (ou session expirée, AC5) → page de connexion ; autre rôle →
 * son propre tableau de bord. Ergonomie uniquement : le backend refuse de toute façon (RG6).
 */
export async function requireRole(locale: Locale, role: Role): Promise<CurrentUser> {
  const user = await getCurrentUser();
  if (!user) redirect(loginPath(locale));
  if (user.role !== role) redirect(dashboardPath(locale, user.role));
  return user;
}
