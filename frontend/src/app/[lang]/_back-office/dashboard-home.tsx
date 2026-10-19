import { connection } from "next/server";
import type { Role } from "@/lib/backoffice/auth";
import { requireRole } from "@/lib/backoffice/session";
import { currentLocale, getDictionary } from "../dictionaries";
import { LogoutButton } from "./logout-button";

/**
 * Accueil back-office d'un rôle (KAN-17 AC1). Rendu bloquant à chaque requête (CSP avec nonce, ADR-0005) : la
 * session est vérifiée avant tout rendu, donc une session absente, expirée (AC5) ou d'un autre rôle donne une vraie
 * redirection HTTP. Les indicateurs du tableau de bord arrivent avec les stories de reporting.
 */
export async function DashboardHome({ role }: { role: Role }) {
  await connection();
  const locale = await currentLocale();
  const dict = await getDictionary();
  const user = await requireRole(locale, role);
  const title = role === "ADMIN" ? dict.backOffice.adminTitle : dict.backOffice.partnerTitle;

  return (
    <main className="mx-auto flex w-full max-w-3xl flex-1 flex-col gap-6 px-4 py-12">
      <h1 className="text-2xl font-semibold">{title}</h1>
      <p>
        {dict.backOffice.signedInAs} <bdi>{user.email}</bdi>
      </p>
      <p>{dict.backOffice.comingSoon}</p>
      <LogoutButton locale={locale} label={dict.backOffice.logout} />
    </main>
  );
}
