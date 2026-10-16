import { cleanup, render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it } from "vitest";
import { Alert, Amount, Button, CardStatus, GiftCard, LanguageSwitcher, RedemptionCode, TextField, maskCode } from ".";

const plain = (s: string | null | undefined) => (s ?? "").replace(/[  ‏‎]/g, " ");

afterEach(cleanup);

describe("Button", () => {
  it("désactive le bouton et l'annonce occupé pendant un chargement (pas de double paiement)", () => {
    render(<Button loading>Payer</Button>);
    const button = screen.getByRole("button", { name: "Payer" });
    expect(button.hasAttribute("disabled")).toBe(true);
    expect(button.getAttribute("aria-busy")).toBe("true");
  });

  it("respecte la cible tactile minimale de 44 px en taille compacte (NFR-071)", () => {
    render(<Button size="sm">Renvoyer</Button>);
    expect(screen.getByRole("button").className).toContain("min-h-control-sm");
  });

  it("est de type button par défaut (ne soumet pas un formulaire par accident)", () => {
    render(<Button>Annuler</Button>);
    expect(screen.getByRole("button").getAttribute("type")).toBe("button");
  });
});

describe("TextField", () => {
  it("relie le libellé et l'aide au champ", () => {
    render(<TextField id="to" label="Email du bénéficiaire" type="email" hint="La carte part après paiement." />);
    const input = screen.getByLabelText("Email du bénéficiaire");
    expect(input.getAttribute("aria-describedby")).toBe("to-hint");
    expect(input.getAttribute("dir")).toBe("ltr");
  });

  it("remplace l'aide par l'erreur et marque le champ invalide", () => {
    render(<TextField id="name" label="Prénom" hint="Aide" error="40 caractères maximum." />);
    const input = screen.getByLabelText("Prénom");
    expect(input.getAttribute("aria-invalid")).toBe("true");
    expect(input.getAttribute("aria-describedby")).toBe("name-error");
    expect(screen.getByRole("alert").textContent).toBe("40 caractères maximum.");
    expect(screen.queryByText("Aide")).toBeNull();
  });
});

describe("Amount", () => {
  it("formate en MAD et isole le montant (bdi)", () => {
    const { container } = render(<Amount amount="1250.00" locale="fr" />);
    const bdi = container.querySelector("bdi");
    expect(bdi).not.toBeNull();
    expect(plain(bdi?.textContent)).toContain("1.250,00");
    expect(plain(bdi?.textContent)).toContain("MAD");
  });

  it("garde les chiffres occidentaux en arabe (usage marocain, US-046)", () => {
    const { container } = render(<Amount amount="500.00" locale="ar" />);
    expect(container.textContent).toMatch(/500/);
    expect(container.textContent).not.toMatch(/[٠-٩]/);
  });
});

describe("CardStatus", () => {
  it("porte un mot en plus de la couleur ; le symbole est masqué aux lecteurs d'écran", () => {
    const { container } = render(<CardStatus status="REDEEMED" label="Utilisée" />);
    expect(container.textContent).toContain("Utilisée");
    expect(container.querySelector('[aria-hidden="true"]')?.textContent).toBe("✓");
  });
});

describe("RedemptionCode", () => {
  it("masque tout sauf les 4 derniers caractères hors page cadeau (NFR-004)", () => {
    expect(maskCode("KD7F-92QX-4M1P")).toBe("••••-••••-4M1P");
    render(<RedemptionCode code="KD7F-92QX-4M1P" masked />);
    expect(screen.queryByText("KD7F-92QX-4M1P")).toBeNull();
    expect(screen.getByText("••••-••••-4M1P").getAttribute("dir")).toBe("ltr");
  });
});

describe("GiftCard", () => {
  it("affiche le message de l'acheteur comme du texte, en direction automatique (US-016)", () => {
    render(
      <GiftCard
        partner="Spa Al Bahia"
        amount="500.00"
        locale="fr"
        expiry="16 octobre 2027"
        expiryLabel="Valable jusqu'au"
        message={'<script>alert("x")</script>'}
      />,
    );
    const quote = screen.getByText('<script>alert("x")</script>');
    expect(quote.tagName).toBe("BLOCKQUOTE");
    expect(quote.getAttribute("dir")).toBe("auto");
  });

  it("affiche le statut quand la carte n'est plus active", () => {
    render(
      <GiftCard
        partner="P"
        amount="200.00"
        locale="fr"
        expiry="1er janvier 2027"
        expiryLabel="Expire"
        status="EXPIRED"
        statusLabel="Expirée"
      />,
    );
    expect(screen.getByText("Expirée")).toBeTruthy();
  });
});

describe("LanguageSwitcher", () => {
  it("marque la langue courante et donne à chaque lien sa langue", () => {
    render(
      <LanguageSwitcher
        current="ar"
        label="اللغة"
        items={[
          { code: "fr", name: "Français", href: "/fr" },
          { code: "ar", name: "العربية", href: "/ar" },
        ]}
      />,
    );
    const ar = screen.getByRole("link", { name: "العربية" });
    expect(ar.getAttribute("aria-current")).toBe("page");
    expect(ar.getAttribute("lang")).toBe("ar-MA");
    expect(screen.getByRole("link", { name: "Français" }).getAttribute("aria-current")).toBeNull();
  });
});

describe("Alert", () => {
  it("annonce les erreurs immédiatement (role=alert) et les succès poliment (role=status)", () => {
    render(
      <>
        <Alert tone="danger" title="Carte expirée" />
        <Alert tone="success" title="Carte utilisée" />
      </>,
    );
    expect(screen.getByRole("alert").textContent).toContain("Carte expirée");
    expect(screen.getByRole("status").textContent).toContain("Carte utilisée");
  });
});
