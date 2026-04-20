package com.gresk.modules.review.application.usecase;

public record SubmitReviewCommand(
        String userId,
        String ticketId,
        String eventId,
        int    artistRating,
        int    soundRating,
        int    ambienceRating,
        int    venueRating,
        int    setlistRating,
        String comment,   // nullable
        String photoUrl   // nullable
) {}
