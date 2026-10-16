package ma.koadooo.i18n;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Représentation publique d'une langue — contracts/openapi.yaml#/components/schemas/Language. */
public record LanguageView(
        String code,
        String nativeName,
        String direction,
        // Nom explicite : sans annotation, un booléen "isX" peut être sérialisé en "x".
        @JsonProperty("isDefault") boolean isDefault) {}
