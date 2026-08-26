package com.gresk.modules.discovery.infrastructure.web;

import com.gresk.modules.discovery.application.dto.ArtistDiscoveryDetail;
import com.gresk.modules.discovery.domain.port.out.ArtistConnection;
import com.gresk.modules.discovery.domain.port.out.DiscoveryArtistSummary;
import com.gresk.modules.discovery.domain.port.out.UpcomingEventInfo;
import org.springframework.stereotype.Component;

@Component
public class DiscoveryResponseMapper {

    public DiscoveryArtistSummaryResponse toResponse(DiscoveryArtistSummary summary) {
        return new DiscoveryArtistSummaryResponse(
                summary.artistId(), summary.name(), summary.origin(), summary.genres(), summary.imageAssetId(),
                summary.sizeTier().name(), summary.spotifyPopularity(), summary.greskScore(), summary.avgRating(),
                summary.greskReviewCount(), summary.greskDemandCount(), summary.hasUpcomingEvents(),
                summary.nextEventDate(), summary.nextEventCity()
        );
    }

    public ArtistDiscoveryDetailResponse toResponse(ArtistDiscoveryDetail detail) {
        ArtistDiscoveryDetailResponse.UpcomingEventResponse nextEvent = detail.nextEvent()
                .map(this::toEventResponse)
                .orElse(null);

        return new ArtistDiscoveryDetailResponse(
                detail.artistId(), detail.name(), detail.origin(), detail.genres(), detail.imageAssetId(),
                detail.bio(), detail.avgRating(), detail.spotifyUrl(), detail.bandcampUrl(),
                detail.sizeTier().name(), detail.spotifyPopularity(), detail.greskScore(),
                detail.greskReviewCount(), detail.greskDemandCount(), detail.greskVerifiedAttendees(),
                detail.knownByCount(), nextEvent
        );
    }

    public ArtistConnectionResponse toResponse(ArtistConnection connection) {
        return new ArtistConnectionResponse(
                connection.artistId(), connection.name(), connection.imageAssetId(), connection.coReviewCount());
    }

    private ArtistDiscoveryDetailResponse.UpcomingEventResponse toEventResponse(UpcomingEventInfo info) {
        return new ArtistDiscoveryDetailResponse.UpcomingEventResponse(
                info.eventId(), info.title(), info.eventDate().toString(), info.city(), info.venue());
    }
}
