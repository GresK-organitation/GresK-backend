package com.gresk.modules.logistics.domain.model;

import java.util.UUID;

public record TourId(UUID value) {

    public TourId {
        if (value == null) throw new IllegalArgumentException("TourId cannot be null");
    }

    public static TourId generate() { return new TourId(UUID.randomUUID()); }

    public static TourId of(String value) {
        try {
            return new TourId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TourId format: " + value, e);
        }
    }

    public static TourId of(UUID value) { return new TourId(value); }

    @Override
    public String toString() { return value.toString(); }
}
