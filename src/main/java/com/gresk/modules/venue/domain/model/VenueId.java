package com.gresk.modules.venue.domain.model;

import java.util.Objects;
import java.util.UUID;

public record VenueId(UUID value) {

    public VenueId {
        Objects.requireNonNull(value, "VenueId value must not be null");
    }

    public static VenueId generate() {
        return new VenueId(UUID.randomUUID());
    }

    public static VenueId of(String value) {
        try {
            return new VenueId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid VenueId format: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
