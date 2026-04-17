package com.gresk.modules.artist.domain.model.valueobject;

import com.gresk.modules.artist.domain.exception.InvalidArtistFeeException;

public record ArtistFee(String value) {

    private static final int MAX_LENGTH = 100;

    public ArtistFee {
        if (value == null) {
            value = "";
        } else {
            value = value.trim();
            if (value.length() > MAX_LENGTH) {
                throw new InvalidArtistFeeException(
                        String.format("Artist fee cannot exceed %d characters", MAX_LENGTH));
            }
        }
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public static ArtistFee of(String value) {
        return new ArtistFee(value);
    }

    public static ArtistFee empty() {
        return new ArtistFee("");
    }

    public static ArtistFee reconstitute(String value) {
        return new ArtistFee(value != null ? value : "");
    }

    @Override
    public String toString() {
        return value;
    }
}
