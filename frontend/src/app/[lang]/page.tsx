import { locales } from "@/i18n/config";
import { formatDateTime } from "@/lib/format";
import { Amount, Khatam, LanguageSwitcher } from "@/components/ui";
import { currentLocale, getDictionary } from "./dictionaries";

export default async function Home() {
  const locale = await currentLocale();
  const dict = await getDictionary();

  return (
    <div className="flex flex-1 flex-col">
      <header className="flex items-center justify-between gap-3 border-b border-line bg-surface-raised px-4 py-3">
        <span className="font-display text-[26px] leading-8 font-bold">Kadoo</span>
        <LanguageSwitcher
          current={locale}
          label={dict.language.label}
          items={locales.map((l) => ({ code: l, name: dict.language[l], href: `/${l}` }))}
        />
      </header>

      <section className="relative overflow-hidden bg-majorelle px-4 pt-8 pb-9 text-on-majorelle">
        <Khatam className="absolute -end-12 -top-14 size-32 fill-safran" />
        <div className="relative mx-auto flex max-w-content flex-col gap-3">
          <h1 className="max-w-[18ch] pe-18 font-display text-[38px] leading-[1.2] font-bold text-balance">
            {dict.home.headline}
          </h1>
          <p className="max-w-reading text-body">{dict.home.subline}</p>
        </div>
      </section>

      <main className="mx-auto flex w-full max-w-content flex-1 flex-col gap-6 px-4 py-8">
        <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-2 text-body-sm">
          <dt className="text-ink-muted">{dict.home.sampleAmountLabel}</dt>
          {/* Montants et dates isolés en LTR dans une phrase RTL (US-046) */}
          <dd>
            <Amount amount="1250.00" locale={locale} />
          </dd>
          <dt className="text-ink-muted">{dict.home.sampleExpiryLabel}</dt>
          <dd>
            <bdi>{formatDateTime("2027-10-16T22:59:59Z", locale)}</bdi>
          </dd>
        </dl>
      </main>
    </div>
  );
}
