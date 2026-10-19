import type { Metadata } from "next";
import { DashboardHome } from "../_back-office/dashboard-home";
import { getDictionary } from "../dictionaries";

/** Back-office : rendu bloquant à chaque requête, pour la CSP avec nonce (ADR-0005). */
export const instant = false;

export async function generateMetadata(): Promise<Metadata> {
  const dict = await getDictionary();
  return { title: dict.backOffice.adminTitle, robots: { index: false } };
}

/** Tableau de bord Admin Kadoo (KAN-17 AC1). */
export default function AdminPage() {
  return <DashboardHome role="ADMIN" />;
}
