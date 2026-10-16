import { cx } from "./cx";

export type CardStatusValue = "ACTIVE" | "REDEEMED" | "EXPIRED" | "BLOCKED" | "CANCELLED";

const STYLE: Record<CardStatusValue, { tone: string; glyph: string }> = {
  ACTIVE: { tone: "bg-menthe-soft text-menthe", glyph: "●" },
  REDEEMED: { tone: "bg-majorelle-soft text-majorelle", glyph: "✓" },
  EXPIRED: { tone: "bg-surface-sunken text-ink-muted", glyph: "◷" },
  BLOCKED: { tone: "bg-safran-soft text-safran-ink", glyph: "‖" },
  CANCELLED: { tone: "bg-grenade-soft text-grenade", glyph: "✕" },
};

export type CardStatusProps = {
  status: CardStatusValue;
  /** Libellé traduit (« Active », « نشطة »…) : la couleur seule ne porte jamais l'information. */
  label: string;
  className?: string;
};

export function CardStatus({ status, label, className }: CardStatusProps) {
  const { tone, glyph } = STYLE[status];
  return (
    <span
      className={cx(
        "inline-flex items-center gap-1 rounded-full py-0.5 ps-2 pe-3 text-body-sm font-medium whitespace-nowrap",
        tone,
        className,
      )}
    >
      <span aria-hidden="true" className="text-[11px] leading-none">
        {glyph}
      </span>
      {label}
    </span>
  );
}
