package com.gresk.modules.review.infrastructure.web;

public record AttendedEventResponse(
        String  ticketId,
        String  eventId,
        String  title,
        String  venue,
        String  coverImageUrl,
        String  genre,
        String  date,              // "2026-04-05" (ISO local date from eventDate)
        boolean pending,
        String  reviewId,          // null when pending
        int     overallRating,     // 0 when pending
        int     artistRating,
        int     soundRating,
        int     ambienceRating,
        int     venueRating,
        int     setlistRating,
        String  comment,
        String  photoUrl,
        int     pointsAwarded,
        double  communityAvgRating
) {}
