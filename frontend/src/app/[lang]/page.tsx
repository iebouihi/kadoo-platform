import Link from "next/link";
import { locales } from "@/i18n/config";
import { formatDateTime, formatMoney } from "@/lib/format";
import { currentLocale, getDictionary } from "./dictionaries";

export default async function Home() {
  const locale = await currentLocale();
  const dict = await getDictionary();

  return (
    <main className="mx-auto flex w-full max-w-3xl flex-1 flex-col gap-8 px-4 py-12">
      <nav aria-label={dict.language.label} className="flex gap-3 text-sm">
        {locales.map((l) => (
          <Link
            key={l}
            href={`/${l}`}
            hrefLang={l}
            aria-current={l === locale ? "page" : undefined}
            className="min-h-11 min-w-11 rounded px-3 py-2 underline-offset-4 hover:underline aria-[current=page]:font-semibold"
          >
            {dict.language[l]}
          </Link>
        ))}
      </nav>

      <section className="flex flex-col gap-4">
        <h1 className="text-3xl font-semibold">{dict.home.headline}</h1>
        <p className="text-lg">{dict.home.subline}</p>
      </section>

      <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-2 text-sm">
        <dt>{dict.home.sampleAmountLabel}</dt>
        {/* Montants et dates isolés en LTR dans une phrase RTL (US-046) */}
        <dd>
          <bdi>{formatMoney("1250.00", locale)}</bdi>
        </dd>
        <dt>{dict.home.sampleExpiryLabel}</dt>
        <dd>
          <bdi>{formatDateTime("2027-10-16T22:59:59Z", locale)}</bdi>
        </dd>
      </dl>
    </main>
  );
}
