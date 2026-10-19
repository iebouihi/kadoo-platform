"use client";

import { useRouter } from "next/navigation";
import type { Locale } from "@/i18n/config";
import { loginPath, postWithCsrf } from "@/lib/backoffice/auth";

export function LogoutButton({ locale, label }: { locale: Locale; label: string }) {
  const router = useRouter();

  async function logout() {
    // Session déjà expirée ou non : dans tous les cas, retour à la connexion.
    await postWithCsrf("/api/v1/auth/logout").catch(() => undefined);
    router.replace(loginPath(locale));
  }

  return (
    <button type="button" onClick={logout} className="min-h-11 self-start rounded border border-current/40 px-4 py-2">
      {label}
    </button>
  );
}
