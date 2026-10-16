import type { Locale } from "@/i18n/config";
import { formatMoney } from "@/lib/format";
import { cx } from "./cx";

export type AmountProps = {
  /** Chaîne décimale reçue de l'API ("1250.00") ; jamais un nombre calculé côté client (NFR-014). */
  amount: string;
  locale: Locale;
  currency?: string;
  size?: "md" | "lg";
  className?: string;
};

/** Montant formaté selon la langue, isolé pour le RTL (US-046). */
export function Amount({ amount, locale, currency, size = "md", className }: AmountProps) {
  return (
    <bdi
      className={cx("font-semibold whitespace-nowrap tabular-nums", size === "lg" && "block text-amount", className)}
    >
      {formatMoney(amount, locale, currency)}
    </bdi>
  );
}
