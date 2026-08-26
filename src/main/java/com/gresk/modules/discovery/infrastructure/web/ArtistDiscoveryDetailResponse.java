package com.gresk.modules.discovery.infrastructure.web;

import java.math.BigDecimal;
import java.util.List;

public record ArtistDiscoveryDetailResponse(
        String artistId,
        String name,
        String origin,
        List<String> genres,
        String imageAssetId,
        String bio,
        double avgRating,
        String spotifyUrl,
        String bandcampUrl,
        String sizeTier,
        Integer spotifyPopularity,
        BigDecimal greskScore,
        int greskReviewCount,
        int greskDemandCount,
        int greskVerifiedAttendees,
        int knownByCount,
        UpcomingEventResponse nextEvent
) {
    public record UpcomingEventResponse(String eventId, String title, String eventDate, String city, String venue) {}
}
