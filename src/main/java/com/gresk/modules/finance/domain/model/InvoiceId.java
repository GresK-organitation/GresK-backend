package com.gresk.modules.finance.domain.model;

import java.util.UUID;

public record InvoiceId(UUID value) {

    public InvoiceId {
        if (value == null) throw new IllegalArgumentException("InvoiceId cannot be null");
    }

    public static InvoiceId generate() {
        return new InvoiceId(UUID.randomUUID());
    }

    public static InvoiceId of(String value) {
        try {
            return new InvoiceId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid InvoiceId format: " + value, e);
        }
    }

    public static InvoiceId of(UUID value) {
        return new InvoiceId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
