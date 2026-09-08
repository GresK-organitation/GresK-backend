package com.gresk.modules.logistics.domain.model;

import java.util.UUID;

public record ItineraryId(UUID value) {

    public ItineraryId {
        if (value == null) throw new IllegalArgumentException("ItineraryId cannot be null");
    }

    public static ItineraryId generate() { return new ItineraryId(UUID.randomUUID()); }

    public static ItineraryId of(String value) {
        try {
            return new ItineraryId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ItineraryId format: " + value, e);
        }
    }

    public static ItineraryId of(UUID value) { return new ItineraryId(value); }

    @Override
    public String toString() { return value.toString(); }
}
