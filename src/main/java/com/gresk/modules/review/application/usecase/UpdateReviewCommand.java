package com.gresk.modules.review.application.usecase;

public record UpdateReviewCommand(
        String reviewId,
        String userId,
        int    artistRating,
        int    soundRating,
        int    ambienceRating,
        int    venueRating,
        int    setlistRating,
        String comment,   // nullable
        String photoUrl   // nullable
) {}
