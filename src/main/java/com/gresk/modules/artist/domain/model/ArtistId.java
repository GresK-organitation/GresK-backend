package com.gresk.modules.artist.domain.model;

import java.util.UUID;

public record ArtistId(UUID value) {

    public ArtistId {
        if (value == null) throw new IllegalArgumentException("ArtistId cannot be null");
    }

    public static ArtistId generate() {
        return new ArtistId(UUID.randomUUID());
    }

    public static ArtistId of(String value) {
        try {
            return new ArtistId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ArtistId format: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
