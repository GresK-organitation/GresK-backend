package com.gresk.modules.artist.infrastructure.web;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ArtistTractionResponse(
        String artistId,
        LocalDate snapshotDate,
        Integer bandsintownFollowers,
        Integer bandsintownUpcomingShows,
        List<CityAudienceResponse> geographicTraction,
        Instant createdAt
) {}
