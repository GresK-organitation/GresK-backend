package com.gresk.modules.event.domain.model;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) {

    public EventId {
        Objects.requireNonNull(value, "EventId value must not be null");
    }

    public static EventId generate() {
        return new EventId(UUID.randomUUID());
    }

    public static EventId of(String value) {
        try {
            return new EventId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EventId format: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
