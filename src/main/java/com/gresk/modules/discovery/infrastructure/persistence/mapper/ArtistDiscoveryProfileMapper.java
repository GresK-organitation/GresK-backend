package com.gresk.modules.discovery.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfileId;
import com.gresk.modules.discovery.domain.model.SizeTier;
import com.gresk.modules.discovery.infrastructure.persistence.entity.ArtistDiscoveryProfileEntity;
import org.springframework.stereotype.Component;

@Component
public class ArtistDiscoveryProfileMapper {

    public ArtistDiscoveryProfile toDomain(ArtistDiscoveryProfileEntity e) {
        return ArtistDiscoveryProfile.reconstitute(
                ArtistDiscoveryProfileId.of(e.getId()),
                ArtistId.of(e.getArtistId()),
                SizeTier.valueOf(e.getSizeTier()),
                e.getSpotifyPopularity(),
                e.getMusicBrainzId(),
                e.getMbCountry(),
                e.getMbCity(),
                e.getMbBeginYear(),
                e.getGreskReviewCount(),
                e.getGreskDemandCount(),
                e.getGreskVerifiedAttendees(),
                e.getKnownByCount(),
                e.getGreskScore(),
                e.isHasUpcomingEvents(),
                e.getNextEventDate(),
                e.getNextEventCity(),
                e.getFirstDiscoveredAt(),
                e.getCalculatedAt()
        );
    }

    public ArtistDiscoveryProfileEntity toEntity(ArtistDiscoveryProfile p) {
        return ArtistDiscoveryProfileEntity.builder()
                .id(p.getId().value())
                .artistId(p.getArtistId().value())
                .sizeTier(p.getSizeTier().name())
                .spotifyPopularity(p.getSpotifyPopularity())
                .musicBrainzId(p.getMusicBrainzId())
                .mbCountry(p.getMbCountry())
                .mbCity(p.getMbCity())
                .mbBeginYear(p.getMbBeginYear())
                .greskReviewCount(p.getGreskReviewCount())
                .greskDemandCount(p.getGreskDemandCount())
                .greskVerifiedAttendees(p.getGreskVerifiedAttendees())
                .knownByCount(p.getKnownByCount())
                .greskScore(p.getGreskScore())
                .hasUpcomingEvents(p.isHasUpcomingEvents())
                .nextEventDate(p.getNextEventDate())
                .nextEventCity(p.getNextEventCity())
                .firstDiscoveredAt(p.getFirstDiscoveredAt())
                .calculatedAt(p.getCalculatedAt())
                .build();
    }
}
