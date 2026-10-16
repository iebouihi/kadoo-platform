import { readFileSync } from "node:fs";
import { resolve } from "node:path";
import { describe, expect, it } from "vitest";

/*
 * Lancé depuis frontend/ (npm test).
 * Le Design System (docs/design/tokens.json) est la référence ; globals.css doit en déclarer exactement les couleurs,
 * en clair et en sombre, pour que le code ne dérive pas des maquettes.
 */
type ColorToken = { name: string; value: string | Record<string, string> };

const tokens = JSON.parse(readFileSync(resolve(process.cwd(), "../docs/design/tokens.json"), "utf8")) as {
  color: { tokens: ColorToken[] };
  radius: { tokens: { name: string; value: string }[] };
};
const css = readFileSync(resolve(process.cwd(), "src/app/[lang]/globals.css"), "utf8");

function block(selector: string): Record<string, string> {
  const start = css.indexOf(selector);
  if (start < 0) throw new Error(`Bloc introuvable : ${selector}`);
  const open = css.indexOf("{", start);
  const close = css.indexOf("}", open);
  const vars: Record<string, string> = {};
  for (const m of css.slice(open + 1, close).matchAll(/--([a-z0-9-]+):\s*([^;]+);/g)) {
    const [, name = "", value = ""] = m;
    vars[name] = value.trim().toLowerCase();
  }
  return vars;
}

const hexTokens = tokens.color.tokens.filter((t) => typeof t.value === "object");
const light = block(":root {");
const dark = block(':root[data-theme="dark"] {');
const darkMedia = block(':root:not([data-theme="light"]) {');

describe("tokens du Design System dans globals.css", () => {
  it.each(hexTokens.map((t) => [t.name, t] as const))("%s : valeurs claire et sombre identiques", (name, t) => {
    const v = t.value as Record<string, string>;
    expect(light[name], `clair : --${name}`).toBe(v.light);
    expect(dark[name], `sombre (data-theme) : --${name}`).toBe(v.dark);
    expect(darkMedia[name], `sombre (prefers-color-scheme) : --${name}`).toBe(v.dark);
  });

  it("déclare chaque couleur comme classe Tailwind (--color-*)", () => {
    for (const t of hexTokens) expect(css).toContain(`--color-${t.name}: var(--${t.name});`);
  });

  it("reprend les rayons du DS", () => {
    for (const r of tokens.radius.tokens.filter((r) => r.name !== "radius-full")) {
      expect(css).toContain(`--${r.name}: ${r.value};`);
    }
  });
});
