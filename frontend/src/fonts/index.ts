import localFont from "next/font/local";

/*
 * Polices du Design System Kadoo, auto-hébergées et réduites aux sous-ensembles latin et arabe (NFR-082).
 * next/font/local ne gère pas un unicode-range par fichier : chaque sous-ensemble est donc une police
 * distincte, et les piles CSS (globals.css) les enchaînent, latin d'abord, arabe ensuite.
 * Les options doivent rester littérales (analyse statique de next/font) : pas de constantes partagées.
 * Licence : SIL Open Font License 1.1 (fichiers OFL-*.txt).
 */

export const readexLatin = localFont({
  src: [
    { path: "./readex-pro-latin-400-normal.woff2", weight: "400", style: "normal" },
    { path: "./readex-pro-latin-500-normal.woff2", weight: "500", style: "normal" },
    { path: "./readex-pro-latin-600-normal.woff2", weight: "600", style: "normal" },
  ],
  variable: "--font-readex-latin",
  display: "swap",
  declarations: [
    {
      prop: "unicode-range",
      value:
        "U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD",
    },
  ],
});

export const readexArabic = localFont({
  src: [
    { path: "./readex-pro-arabic-400-normal.woff2", weight: "400", style: "normal" },
    { path: "./readex-pro-arabic-500-normal.woff2", weight: "500", style: "normal" },
    { path: "./readex-pro-arabic-600-normal.woff2", weight: "600", style: "normal" },
  ],
  variable: "--font-readex-arabic",
  display: "swap",
  declarations: [
    {
      prop: "unicode-range",
      value:
        "U+0600-06FF, U+0750-077F, U+0870-088E, U+0890-0891, U+0897-08E1, U+08E3-08FF, U+200C-200E, U+2010-2011, U+204F, U+2E41, U+FB50-FDFF, U+FE70-FE74, U+FE76-FEFC",
    },
  ],
});

export const reemKufiLatin = localFont({
  src: [
    { path: "./reem-kufi-latin-600-normal.woff2", weight: "600", style: "normal" },
    { path: "./reem-kufi-latin-700-normal.woff2", weight: "700", style: "normal" },
  ],
  variable: "--font-reem-latin",
  display: "swap",
  preload: false,
  declarations: [
    {
      prop: "unicode-range",
      value:
        "U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD",
    },
  ],
});

export const reemKufiArabic = localFont({
  src: [
    { path: "./reem-kufi-arabic-600-normal.woff2", weight: "600", style: "normal" },
    { path: "./reem-kufi-arabic-700-normal.woff2", weight: "700", style: "normal" },
  ],
  variable: "--font-reem-arabic",
  display: "swap",
  preload: false,
  declarations: [
    {
      prop: "unicode-range",
      value:
        "U+0600-06FF, U+0750-077F, U+0870-088E, U+0890-0891, U+0897-08E1, U+08E3-08FF, U+200C-200E, U+2010-2011, U+204F, U+2E41, U+FB50-FDFF, U+FE70-FE74, U+FE76-FEFC",
    },
  ],
});

export const plexMono = localFont({
  src: [{ path: "./ibm-plex-mono-latin-500-normal.woff2", weight: "500", style: "normal" }],
  variable: "--font-plex-mono",
  display: "swap",
  preload: false,
});

/** Classes à poser sur <html> : elles déclarent les variables CSS des polices. */
export const fontVariables = [readexLatin, readexArabic, reemKufiLatin, reemKufiArabic, plexMono]
  .map((f) => f.variable)
  .join(" ");
