package com.gresk.modules.finance.domain.model;

import java.util.UUID;

public record SupplierInvoiceId(UUID value) {

    public SupplierInvoiceId {
        if (value == null) throw new IllegalArgumentException("SupplierInvoiceId cannot be null");
    }

    public static SupplierInvoiceId generate() {
        return new SupplierInvoiceId(UUID.randomUUID());
    }

    public static SupplierInvoiceId of(String value) {
        try {
            return new SupplierInvoiceId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid SupplierInvoiceId format: " + value, e);
        }
    }

    public static SupplierInvoiceId of(UUID value) {
        return new SupplierInvoiceId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
