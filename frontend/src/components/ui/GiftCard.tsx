import type { Locale } from "@/i18n/config";
import { Amount } from "./Amount";
import { CardStatus, type CardStatusValue } from "./CardStatus";
import { Khatam } from "./Khatam";
import { cx } from "./cx";

export type GiftCardProps = {
  partner: string;
  offer?: string;
  amount: string;
  locale: Locale;
  /** Date déjà formatée dans le fuseau Africa/Casablanca (formatDateTime / formatDate). */
  expiry: string;
  expiryLabel: string;
  /** Message de l'acheteur, affiché comme du texte, direction automatique (US-016). */
  message?: string;
  from?: string;
  fromLabel?: string;
  status?: CardStatusValue;
  statusLabel?: string;
  className?: string;
};

/** La carte cadeau (récapitulatif, page cadeau, caisse). Le code d'utilisation n'y figure pas : voir RedemptionCode. */
export function GiftCard(props: GiftCardProps) {
  const muted = props.status !== undefined && props.status !== "ACTIVE";
  return (
    <article
      className={cx(
        "w-full max-w-[380px] overflow-hidden rounded-lg border border-line bg-surface-raised shadow-gift",
        props.className,
      )}
    >
      <div className={cx("relative h-22 overflow-hidden", muted ? "bg-surface-sunken" : "bg-majorelle")}>
        <Khatam className={cx("absolute -end-6 -top-7 size-30", muted ? "fill-line-strong" : "fill-safran")} />
        <span
          className={cx(
            "absolute start-6 bottom-3 font-display text-[22px] leading-7 font-bold",
            muted ? "text-ink-muted" : "text-on-majorelle",
          )}
        >
          Kadoo
        </span>
      </div>
      <div className="grid gap-4 p-6">
        <div className="flex items-start justify-between gap-3">
          <div>
            <p className="text-subheading font-semibold">{props.partner}</p>
            {props.offer ? <p className="text-body-sm text-ink-muted">{props.offer}</p> : null}
          </div>
          {props.status && props.statusLabel ? <CardStatus status={props.status} label={props.statusLabel} /> : null}
        </div>
        <Amount amount={props.amount} locale={props.locale} size="lg" />
        {props.message ? (
          <blockquote dir="auto" className="border-s-2 border-safran ps-3 text-body whitespace-pre-line">
            {props.message}
          </blockquote>
        ) : null}
        <dl className="flex flex-wrap gap-6">
          <div>
            <dt className="text-caption font-medium tracking-[0.04em] text-ink-muted uppercase rtl:normal-case">
              {props.expiryLabel}
            </dt>
            <dd className="text-body-sm font-medium">
              <bdi>{props.expiry}</bdi>
            </dd>
          </div>
          {props.from && props.fromLabel ? (
            <div>
              <dt className="text-caption font-medium tracking-[0.04em] text-ink-muted uppercase rtl:normal-case">
                {props.fromLabel}
              </dt>
              <dd className="text-body-sm font-medium">{props.from}</dd>
            </div>
          ) : null}
        </dl>
      </div>
    </article>
  );
}
