/**
 * CSP stricte du back-office (NFR-001, ADR-0005) : scripts et styles autorisés uniquement par nonce, renouvelé à
 * chaque requête par src/proxy.ts. En développement, React a besoin de 'unsafe-eval' (jamais en production).
 */
export function backOfficeCsp(nonce: string, isDev: boolean): string {
  return [
    "default-src 'self'",
    `script-src 'self' 'nonce-${nonce}' 'strict-dynamic'${isDev ? " 'unsafe-eval'" : ""}`,
    `style-src 'self' 'nonce-${nonce}'`,
    "img-src 'self' blob: data:",
    "font-src 'self'",
    "connect-src 'self'",
    "object-src 'none'",
    "base-uri 'self'",
    "form-action 'self'",
    "frame-ancestors 'none'",
  ].join("; ");
}
