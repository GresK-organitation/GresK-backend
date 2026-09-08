package com.gresk.modules.show.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ShowId(UUID value) {

    public ShowId {
        Objects.requireNonNull(value, "ShowId value must not be null");
    }

    public static ShowId generate() {
        return new ShowId(UUID.randomUUID());
    }

    public static ShowId of(String value) {
        try {
            return new ShowId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ShowId format: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
