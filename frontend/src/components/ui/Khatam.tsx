/** Étoile à huit branches du zellige (khatam), seul motif du DS : deux carrés superposés tournés de 45°. */
export function Khatam({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 120 120" aria-hidden="true" focusable="false" className={className}>
      <rect x="25" y="25" width="70" height="70" rx="4" />
      <rect x="25" y="25" width="70" height="70" rx="4" transform="rotate(45 60 60)" />
    </svg>
  );
}
