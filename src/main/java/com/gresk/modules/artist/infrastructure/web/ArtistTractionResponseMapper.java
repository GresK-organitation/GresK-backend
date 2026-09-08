package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.domain.model.ArtistTractionSnapshot;
import org.springframework.stereotype.Component;

@Component
public class ArtistTractionResponseMapper {

    public ArtistTractionResponse toResponse(ArtistTractionSnapshot s) {
        var entries = s.geographicTraction().entries().stream()
                .map(c -> new CityAudienceResponse(c.city(), c.country(), c.audienceScore(), c.source().name()))
                .toList();

        return new ArtistTractionResponse(
                s.artistId().value().toString(),
                s.snapshotDate(),
                s.bandsintownFollowers(),
                s.bandsintownUpcomingShows(),
                entries,
                s.createdAt()
        );
    }
}
