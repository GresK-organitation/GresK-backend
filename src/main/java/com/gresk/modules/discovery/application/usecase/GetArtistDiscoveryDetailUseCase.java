package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.application.dto.ArtistDiscoveryDetail;
import com.gresk.modules.discovery.domain.exception.ArtistDiscoveryProfileNotFoundException;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;
import com.gresk.modules.discovery.domain.model.SizeTier;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogInfo;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogPort;
import com.gresk.modules.discovery.domain.port.out.ArtistDiscoveryProfileRepository;
import com.gresk.modules.discovery.domain.port.out.EventCatalogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetArtistDiscoveryDetailUseCase {

    private final ArtistCatalogPort artistCatalogPort;
    private final ArtistDiscoveryProfileRepository profileRepository;
    private final EventCatalogPort eventCatalogPort;

    public ArtistDiscoveryDetail execute(ArtistId artistId) {
        ArtistCatalogInfo catalogInfo = artistCatalogPort.findById(artistId)
                .orElseThrow(() -> new ArtistDiscoveryProfileNotFoundException(
                        "Artist not found in catalog: " + artistId));

        ArtistDiscoveryProfile profile = profileRepository.findByArtistId(artistId)
                .orElseGet(() -> emptyProfile(artistId));

        return new ArtistDiscoveryDetail(
                catalogInfo.artistId(),
                catalogInfo.name(),
                catalogInfo.origin(),
                catalogInfo.genres(),
                catalogInfo.imageAssetId(),
                catalogInfo.bio(),
                catalogInfo.avgRating(),
                catalogInfo.spotifyUrl(),
                catalogInfo.bandcampUrl(),
                profile.getSizeTier(),
                profile.getSpotifyPopularity(),
                profile.getGreskScore(),
                profile.getGreskReviewCount(),
                profile.getGreskDemandCount(),
                profile.getGreskVerifiedAttendees(),
                profile.getKnownByCount(),
                eventCatalogPort.findNextUpcomingEvent(artistId)
        );
    }

    /** El artista aún no tiene perfil calculado (job semanal todavía no ha corrido). */
    private ArtistDiscoveryProfile emptyProfile(ArtistId artistId) {
        return ArtistDiscoveryProfile.reconstitute(
                com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfileId.generate(),
                artistId, SizeTier.NO_SPOTIFY, null, null, null, null, null,
                0, 0, 0, 0, BigDecimal.ZERO, false, null, null,
                java.time.Instant.now(), java.time.Instant.now()
        );
    }
}
