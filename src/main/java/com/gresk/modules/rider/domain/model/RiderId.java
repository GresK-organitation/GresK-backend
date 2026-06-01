package com.gresk.modules.rider.domain.model;

import java.util.UUID;

public record RiderId(UUID value) {

    public RiderId {
        if (value == null) throw new IllegalArgumentException("RiderId cannot be null");
    }

    public static RiderId generate() {
        return new RiderId(UUID.randomUUID());
    }

    public static RiderId of(String value) {
        try {
            return new RiderId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid RiderId format: " + value, e);
        }
    }

    public static RiderId of(UUID value) {
        return new RiderId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
