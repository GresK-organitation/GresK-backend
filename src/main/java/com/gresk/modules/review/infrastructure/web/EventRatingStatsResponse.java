package com.gresk.modules.review.infrastructure.web;

public record EventRatingStatsResponse(
        int    reviewCount,
        double avgOverallRating,
        double avgArtistRating,
        double avgSoundRating,
        double avgAmbienceRating,
        double avgVenueRating,
        double avgSetlistRating
) {}
