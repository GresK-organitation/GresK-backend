package com.gresk.modules.discovery.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DiscoveryArtistSummaryResponse(
        String artistId,
        String name,
        String origin,
        List<String> genres,
        String imageAssetId,
        String sizeTier,
        Integer spotifyPopularity,
        BigDecimal greskScore,
        double avgRating,
        int greskReviewCount,
        int greskDemandCount,
        boolean hasUpcomingEvents,
        LocalDate nextEventDate,
        String nextEventCity
) {}
