import Link from "next/link";
import { bcp47, type Locale } from "@/i18n/config";
import { cx } from "./cx";

export type LanguageSwitcherProps = {
  current: Locale;
  /** Chaque langue nommée dans sa propre langue (Français, العربية, English) ou en abrégé (FR, عربي, EN). */
  items: { code: Locale; name: string; href: string }[];
  /** aria-label traduit du menu. */
  label: string;
  className?: string;
};

/** Choix de la langue (US-040). Le lien mène à la même page dans l'autre langue. */
export function LanguageSwitcher({ current, items, label, className }: LanguageSwitcherProps) {
  return (
    <nav aria-label={label} className={cx("inline-flex gap-0.5 rounded-full bg-surface-sunken p-0.5", className)}>
      {items.map((item) => {
        const isCurrent = item.code === current;
        return (
          <Link
            key={item.code}
            href={item.href}
            hrefLang={bcp47[item.code]}
            lang={bcp47[item.code]}
            aria-current={isCurrent ? "page" : undefined}
            className={cx(
              "inline-flex min-h-control-sm items-center rounded-full px-4 text-body-sm font-medium no-underline",
              isCurrent ? "bg-surface-raised text-ink shadow-card" : "text-ink-muted hover:text-ink",
            )}
          >
            {item.name}
          </Link>
        );
      })}
    </nav>
  );
}
