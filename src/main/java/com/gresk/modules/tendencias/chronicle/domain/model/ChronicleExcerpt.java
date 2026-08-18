package com.gresk.modules.tendencias.chronicle.domain.model;

import java.util.Objects;

/**
 * Invariante de dominio anti-scraping: el dominio no puede representar un
 * extracto más largo que un par de líneas. La sanitización de HTML del feed
 * ocurre en infraestructura, antes de construir este value object.
 */
public record ChronicleExcerpt(String value) {

    public static final int MAX_LENGTH = 220; // ~2 líneas

    public ChronicleExcerpt {
        Objects.requireNonNull(value, "Excerpt must not be null");
        if (value.isBlank()) throw new IllegalArgumentException("Excerpt must not be blank");
        if (value.length() > MAX_LENGTH)
            throw new IllegalArgumentException("Excerpt exceeds max length of " + MAX_LENGTH + " chars");
    }

    /** Único punto de entrada recomendado: trunca texto ya plano, nunca lanza por longitud. */
    public static ChronicleExcerpt truncate(String plainText) {
        String trimmed = plainText == null ? "" : plainText.trim();
        String result = trimmed.length() <= MAX_LENGTH
                ? trimmed
                : trimmed.substring(0, MAX_LENGTH - 1).stripTrailing() + "…";
        return new ChronicleExcerpt(result);
    }
}
