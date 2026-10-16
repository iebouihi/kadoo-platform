import { cx } from "./cx";

export type RedemptionCodeProps = {
  /** Code en clair : uniquement sur la page cadeau du bénéficiaire (US-023). */
  code: string;
  label?: string;
  /** Ne montre que les 4 derniers caractères (back-office, support ; NFR-004). */
  masked?: boolean;
  className?: string;
};

export function maskCode(code: string): string {
  return code.slice(0, -4).replace(/[A-Z0-9]/gi, "•") + code.slice(-4);
}

export function RedemptionCode({ code, label, masked = false, className }: RedemptionCodeProps) {
  return (
    <div
      className={cx(
        "grid justify-items-center gap-2 rounded-md border-2 border-dashed border-line-strong bg-surface-raised px-6 py-4",
        className,
      )}
    >
      {label ? <p className="text-body-sm text-ink-muted">{label}</p> : null}
      <p dir="ltr" translate="no" className="font-mono text-code font-medium tracking-[0.12em] text-ink">
        {masked ? maskCode(code) : code}
      </p>
    </div>
  );
}
