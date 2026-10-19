"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import type { Locale } from "@/i18n/config";
import { type Role, dashboardPath, postWithCsrf } from "@/lib/backoffice/auth";

type Labels = {
  email: string;
  password: string;
  submit: string;
  submitting: string;
  invalidCredentials: string;
  unexpectedError: string;
};

/**
 * Formulaire de connexion back-office (KAN-17). AC2 : un seul message en cas de refus, quel qu'en soit le motif ;
 * le backend renvoie la même réponse pour un email inconnu, un mauvais mot de passe ou un compte verrouillé.
 */
export function LoginForm({ locale, labels }: { locale: Locale; labels: Labels }) {
  const router = useRouter();
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  async function onSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setPending(true);
    setError(null);
    try {
      const response = await postWithCsrf("/api/v1/auth/login", {
        email: form.get("email"),
        password: form.get("password"),
      });
      if (response.ok) {
        const { role } = (await response.json()) as { role: Role };
        router.replace(dashboardPath(locale, role));
        return;
      }
      setError(response.status === 401 ? labels.invalidCredentials : labels.unexpectedError);
    } catch {
      setError(labels.unexpectedError);
    }
    setPending(false);
  }

  return (
    <form onSubmit={onSubmit} className="flex flex-col gap-4" noValidate={false}>
      <label className="flex flex-col gap-1">
        <span>{labels.email}</span>
        <input
          name="email"
          type="email"
          autoComplete="username"
          required
          maxLength={254}
          dir="ltr"
          className="min-h-11 rounded border border-current/40 px-3 py-2"
        />
      </label>
      <label className="flex flex-col gap-1">
        <span>{labels.password}</span>
        <input
          name="password"
          type="password"
          autoComplete="current-password"
          required
          maxLength={200}
          className="min-h-11 rounded border border-current/40 px-3 py-2"
        />
      </label>
      {error && (
        <p role="alert" className="text-red-700 dark:text-red-400">
          {error}
        </p>
      )}
      <button
        type="submit"
        disabled={pending}
        className="min-h-11 rounded bg-foreground px-4 py-2 font-semibold text-background disabled:opacity-60"
      >
        {pending ? labels.submitting : labels.submit}
      </button>
    </form>
  );
}
