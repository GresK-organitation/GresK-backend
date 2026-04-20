package com.gresk.modules.review.domain.model;

import java.util.UUID;

public record ReviewId(UUID value) {

    public ReviewId {
        if (value == null) throw new IllegalArgumentException("ReviewId must not be null");
    }

    public static ReviewId generate() { return new ReviewId(UUID.randomUUID()); }

    public static ReviewId of(String value) {
        try { return new ReviewId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ReviewId: " + value);
        }
    }

    public static ReviewId of(UUID value) { return new ReviewId(value); }
}
