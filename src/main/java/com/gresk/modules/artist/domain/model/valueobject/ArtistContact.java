package com.gresk.modules.artist.domain.model.valueobject;

import com.gresk.modules.artist.domain.exception.InvalidArtistContactException;

public record ArtistContact(String value) {

    private static final int MAX_LENGTH = 255;

    public ArtistContact {
        if (value == null || value.isBlank()) {
            throw new InvalidArtistContactException("Artist contact cannot be null or blank");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new InvalidArtistContactException(
                    String.format("Artist contact cannot exceed %d characters", MAX_LENGTH));
        }
    }

    public static ArtistContact of(String value) {
        return new ArtistContact(value);
    }

    public static ArtistContact reconstitute(String value) {
        return new ArtistContact(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
