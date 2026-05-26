package com.gresk.modules.review.infrastructure.web;

public record ReviewResponse(
        String  reviewId,
        String  eventId,
        String  ticketId,
        int     artistRating,
        int     soundRating,
        int     ambienceRating,
        int     venueRating,
        int     setlistRating,
        int     overallRating,
        String  comment,
        String  photoUrl,
        int     pointsAwarded,
        String  status,
        String  createdAt,
        int     likeCount,
        boolean likedByCurrentUser
) {}
