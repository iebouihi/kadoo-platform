import { defineConfig, globalIgnores } from "eslint/config";
import nextVitals from "eslint-config-next/core-web-vitals";
import nextTs from "eslint-config-next/typescript";

const eslintConfig = defineConfig([
  ...nextVitals,
  ...nextTs,
  {
    rules: {
      // NFR-008 : aucun HTML injecté depuis des données utilisateur
      "react/no-danger": "error",
      // NFR-014 : pas de calcul monétaire côté client avec parseFloat
      "no-restricted-globals": ["error", { name: "parseFloat", message: "Montants : chaîne décimale, aucun calcul côté client (NFR-014)." }],
      "@typescript-eslint/no-explicit-any": "error",
    },
  },
  globalIgnores([".next/**", "out/**", "build/**", "next-env.d.ts", "src/lib/api/schema.d.ts", "playwright-report/**", "test-results/**"]),
]);

export default eslintConfig;
