package com.gresk.modules.review.domain.model;

import java.util.Objects;

public record DetailedRating(
        Rating artistRating,
        Rating soundRating,
        Rating ambienceRating,
        Rating venueRating,
        Rating setlistRating
) {
    public DetailedRating {
        Objects.requireNonNull(artistRating,   "artistRating is required");
        Objects.requireNonNull(soundRating,    "soundRating is required");
        Objects.requireNonNull(ambienceRating, "ambienceRating is required");
        Objects.requireNonNull(venueRating,    "venueRating is required");
        Objects.requireNonNull(setlistRating,  "setlistRating is required");
    }

    /** Calculated average rounded to nearest integer (1-5). */
    public Rating overall() {
        int avg = (int) Math.round(
            (artistRating.value() + soundRating.value() + ambienceRating.value()
             + venueRating.value() + setlistRating.value()) / 5.0
        );
        return Rating.of(avg);
    }

    public static DetailedRating of(int artist, int sound, int ambience,
                                    int venue, int setlist) {
        return new DetailedRating(
            Rating.of(artist), Rating.of(sound), Rating.of(ambience),
            Rating.of(venue),  Rating.of(setlist)
        );
    }

    public static DetailedRating reconstitute(int artist, int sound, int ambience,
                                              int venue, int setlist) {
        return new DetailedRating(
            Rating.reconstitute(artist),   Rating.reconstitute(sound),
            Rating.reconstitute(ambience), Rating.reconstitute(venue),
            Rating.reconstitute(setlist)
        );
    }
}
