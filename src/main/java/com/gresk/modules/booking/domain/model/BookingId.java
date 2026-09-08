package com.gresk.modules.booking.domain.model;

import java.util.Objects;
import java.util.UUID;

public record BookingId(UUID value) {

    public BookingId {
        Objects.requireNonNull(value, "BookingId value must not be null");
    }

    public static BookingId generate() {
        return new BookingId(UUID.randomUUID());
    }

    public static BookingId of(String value) {
        try {
            return new BookingId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid BookingId format: " + value, e);
        }
    }

    public static BookingId of(UUID value) {
        return new BookingId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
