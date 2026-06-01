package com.gresk.modules.rider.domain.model;

import java.util.UUID;

public record AlertId(UUID value) {

    public AlertId {
        if (value == null) throw new IllegalArgumentException("AlertId cannot be null");
    }

    public static AlertId generate() {
        return new AlertId(UUID.randomUUID());
    }

    public static AlertId of(String value) {
        try {
            return new AlertId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AlertId format: " + value, e);
        }
    }

    public static AlertId of(UUID value) {
        return new AlertId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
