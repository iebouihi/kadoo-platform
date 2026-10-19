import type { Metadata } from "next";
import { connection } from "next/server";
import { currentLocale, getDictionary } from "../dictionaries";
import { LoginForm } from "./login-form";

/** Back-office : rendu bloquant à chaque requête, pour la CSP avec nonce (ADR-0005). */
export const instant = false;

export async function generateMetadata(): Promise<Metadata> {
  const dict = await getDictionary();
  return { title: dict.login.title, robots: { index: false } };
}

/** Connexion back-office, Admin et gestionnaires partenaires (KAN-17). */
export default async function LoginPage() {
  // Rendu à chaque requête : la CSP du back-office exige un nonce par requête (ADR-0005).
  await connection();
  const locale = await currentLocale();
  const dict = await getDictionary();

  return (
    <main className="mx-auto flex w-full max-w-sm flex-1 flex-col justify-center gap-6 px-4 py-12">
      <h1 className="text-2xl font-semibold">{dict.login.title}</h1>
      <LoginForm locale={locale} labels={dict.login} />
    </main>
  );
}
