package com.gresk.modules.journal.domain.model;

import com.gresk.modules.journal.domain.exception.InvalidRatingCriterionException;

/**
 * A user-defined valuation criterion (e.g. "luces", "interacción con el público"),
 * freeform per entry — not backed by a fixed catalog of criteria.
 */
public record RatingCriterion(String label, int value) {

    private static final int MAX_LABEL_LENGTH = 60;

    public RatingCriterion {
        if (label == null || label.isBlank()) {
            throw new InvalidRatingCriterionException("Criterion label must not be blank");
        }
        if (label.length() > MAX_LABEL_LENGTH) {
            throw new InvalidRatingCriterionException(
                    "Criterion label must not exceed " + MAX_LABEL_LENGTH + " characters");
        }
        if (value < 1 || value > 5) {
            throw new InvalidRatingCriterionException(
                    "Criterion value must be between 1 and 5, got: " + value);
        }
        label = label.strip();
    }

    public static RatingCriterion of(String label, int value) {
        return new RatingCriterion(label, value);
    }
}
