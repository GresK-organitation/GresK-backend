package com.gresk.modules.supplier.domain.model;

import java.util.UUID;

public record SupplierId(UUID value) {

    public SupplierId {
        if (value == null) throw new IllegalArgumentException("SupplierId cannot be null");
    }

    public static SupplierId generate() {
        return new SupplierId(UUID.randomUUID());
    }

    public static SupplierId of(String value) {
        try {
            return new SupplierId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid SupplierId format: " + value, e);
        }
    }

    public static SupplierId of(UUID value) {
        return new SupplierId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
