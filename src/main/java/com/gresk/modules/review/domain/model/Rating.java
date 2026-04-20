package com.gresk.modules.review.domain.model;

import com.gresk.modules.review.domain.exception.InvalidRatingException;

public record Rating(int value) {

    public Rating {
        if (value < 1 || value > 5)
            throw new InvalidRatingException("Rating must be between 1 and 5, got: " + value);
    }

    public static Rating of(int value)           { return new Rating(value); }
    public static Rating reconstitute(int value) { return new Rating(value); }
}
