package com.gresk.modules.quotation.domain.model;

import java.util.UUID;

public record QuoteId(UUID value) {

    public QuoteId {
        if (value == null) throw new IllegalArgumentException("QuoteId cannot be null");
    }

    public static QuoteId generate() {
        return new QuoteId(UUID.randomUUID());
    }

    public static QuoteId of(String value) {
        try {
            return new QuoteId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid QuoteId format: " + value, e);
        }
    }

    public static QuoteId of(UUID value) {
        return new QuoteId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
