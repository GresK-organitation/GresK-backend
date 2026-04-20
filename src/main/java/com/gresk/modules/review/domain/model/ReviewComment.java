package com.gresk.modules.review.domain.model;

public record ReviewComment(String value) {

    private static final int MAX_LENGTH = 300;

    public ReviewComment {
        if (value != null && value.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                "Review comment must not exceed " + MAX_LENGTH + " characters");
        value = (value == null || value.isBlank()) ? null : value.strip();
    }

    public boolean isEmpty()                             { return value == null; }
    public static ReviewComment empty()                  { return new ReviewComment(null); }
    public static ReviewComment of(String value)         { return new ReviewComment(value); }
    public static ReviewComment reconstitute(String value) { return new ReviewComment(value); }
}
