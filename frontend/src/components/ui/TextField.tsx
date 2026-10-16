import type { ComponentProps } from "react";
import { cx } from "./cx";

export type TextFieldProps = Omit<ComponentProps<"input">, "id"> & {
  /** Obligatoire : relie le libellé, l'aide et l'erreur. */
  id: string;
  /** Libellé visible, traduit. */
  label: string;
  hint?: string;
  /** Erreur traduite : dit quoi corriger. */
  error?: string;
};

const LTR_TYPES = new Set(["email", "tel", "url"]);

export function TextField({ id, label, hint, error, type = "text", className, ...rest }: TextFieldProps) {
  const hintId = hint && !error ? `${id}-hint` : undefined;
  const errorId = error ? `${id}-error` : undefined;
  return (
    <div className={cx("grid gap-2", className)}>
      <label htmlFor={id} className="text-body-sm font-medium text-ink">
        {label}
      </label>
      <input
        id={id}
        type={type}
        dir={LTR_TYPES.has(type) ? "ltr" : undefined}
        aria-invalid={error ? true : undefined}
        aria-describedby={[hintId, errorId].filter(Boolean).join(" ") || undefined}
        {...rest}
        className={cx(
          "min-h-control rounded-sm bg-surface-raised px-3 text-body text-ink placeholder:text-ink-muted",
          LTR_TYPES.has(type) && "text-start rtl:text-end",
          error ? "border-2 border-grenade" : "border border-line-strong focus-visible:border-majorelle",
        )}
      />
      {hintId ? (
        <p id={hintId} className="text-body-sm text-ink-muted">
          {hint}
        </p>
      ) : null}
      {errorId ? (
        <p id={errorId} role="alert" className="text-body-sm font-medium text-grenade">
          {error}
        </p>
      ) : null}
    </div>
  );
}
