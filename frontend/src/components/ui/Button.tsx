import Link from "next/link";
import type { ComponentProps, ReactNode } from "react";
import { cx } from "./cx";

export type ButtonVariant = "primary" | "gift" | "secondary" | "ghost" | "danger";
export type ButtonSize = "md" | "sm";

type Common = {
  /** primary = action principale (une par écran) ; gift = « Offrir / Payer » uniquement ; danger = bloquer, annuler. */
  variant?: ButtonVariant;
  /** md = 48 px, sm = 44 px (jamais moins, NFR-071). */
  size?: ButtonSize;
  /** Pleine largeur (bas de formulaire mobile). */
  block?: boolean;
};

const VARIANTS: Record<ButtonVariant, string> = {
  primary: "bg-majorelle text-on-majorelle hover:brightness-110",
  gift: "bg-safran text-on-safran hover:brightness-105",
  secondary: "border-line-strong bg-surface-raised text-ink hover:border-majorelle hover:text-majorelle",
  ghost: "bg-transparent text-majorelle hover:bg-majorelle-soft",
  danger: "border-grenade bg-surface-raised text-grenade",
};

export function buttonClasses({ variant = "primary", size = "md", block = false }: Common = {}): string {
  return cx(
    "inline-flex items-center justify-center gap-2 rounded-md border border-transparent font-semibold no-underline transition-colors",
    "disabled:cursor-not-allowed disabled:opacity-55 aria-disabled:cursor-not-allowed aria-disabled:opacity-55",
    size === "sm" ? "min-h-control-sm px-4 text-body-sm" : "min-h-control px-6 text-body",
    block && "w-full",
    VARIANTS[variant],
  );
}

export type ButtonProps = Common &
  ComponentProps<"button"> & {
    /** Affiche une roue et désactive le bouton (paiement, validation : pas de double envoi). */
    loading?: boolean;
  };

export function Button({
  variant,
  size,
  block,
  loading = false,
  disabled,
  className,
  children,
  type = "button",
  ...rest
}: ButtonProps) {
  return (
    <button
      type={type}
      {...rest}
      disabled={disabled || loading}
      aria-busy={loading || undefined}
      className={cx(buttonClasses({ variant, size, block }), className)}
    >
      {loading ? (
        <span
          aria-hidden="true"
          className="size-4 animate-spin rounded-full border-2 border-current border-e-transparent"
        />
      ) : null}
      {children}
    </button>
  );
}

export type ButtonLinkProps = Common & ComponentProps<typeof Link> & { children: ReactNode };

/** Lien qui a l'apparence d'un bouton (navigation : « Offrir cette carte » vers le formulaire). */
export function ButtonLink({ variant, size, block, className, ...rest }: ButtonLinkProps) {
  return <Link {...rest} className={cx(buttonClasses({ variant, size, block }), className)} />;
}
