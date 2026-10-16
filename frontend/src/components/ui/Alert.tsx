import type { ReactNode } from "react";
import { cx } from "./cx";

export type AlertTone = "info" | "success" | "warning" | "danger";

const TONES: Record<AlertTone, { tone: string; glyph: string }> = {
  info: { tone: "bg-majorelle-soft text-majorelle", glyph: "i" },
  success: { tone: "bg-menthe-soft text-menthe", glyph: "✓" },
  warning: { tone: "bg-safran-soft text-safran-ink", glyph: "!" },
  danger: { tone: "bg-grenade-soft text-grenade", glyph: "✕" },
};

export type AlertProps = {
  tone?: AlertTone;
  /** Titre court qui dit l'état. */
  title?: string;
  /** Ce que la personne doit faire ensuite. */
  children?: ReactNode;
  className?: string;
};

export function Alert({ tone = "info", title, children, className }: AlertProps) {
  const { tone: toneClass, glyph } = TONES[tone];
  return (
    <div
      role={tone === "danger" || tone === "warning" ? "alert" : "status"}
      className={cx("flex items-start gap-3 rounded-sm px-4 py-3", toneClass, className)}
    >
      <span
        aria-hidden="true"
        className="inline-grid size-[22px] flex-none place-items-center rounded-full border-2 border-current text-[13px] leading-none font-bold"
      >
        {glyph}
      </span>
      <div>
        {title ? <p className="font-semibold">{title}</p> : null}
        {children ? <div className="text-body-sm text-ink">{children}</div> : null}
      </div>
    </div>
  );
}
