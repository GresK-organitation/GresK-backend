package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.exception.ArtistDiscoveryProfileNotFoundException;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogInfo;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogPort;
import com.gresk.modules.discovery.domain.port.out.ArtistDiscoveryProfileRepository;
import com.gresk.modules.discovery.domain.port.out.ArtistPopularitySnapshotPort;
import com.gresk.modules.discovery.domain.port.out.CommunitySignals;
import com.gresk.modules.discovery.domain.port.out.CommunitySignalsPort;
import com.gresk.modules.discovery.domain.port.out.DemandSignalRepository;
import com.gresk.modules.discovery.domain.port.out.DiscoveryProfileSignals;
import com.gresk.modules.discovery.domain.port.out.EventCatalogPort;
import com.gresk.modules.discovery.domain.port.out.UpcomingEventInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Recalcula por completo el perfil de descubrimiento de un artista a partir
 * de las señales agregadas de todos los módulos fuente. Los campos de
 * MusicBrainz se preservan entre recálculos (los enriquece un job mensual
 * aparte) — nunca se pisan aquí.
 */
@Service
@RequiredArgsConstructor
public class RecalculateArtistDiscoveryProfileUseCase {

    private static final int MOMENTUM_WINDOW_DAYS = 28;

    private final ArtistCatalogPort artistCatalogPort;
    private final ArtistPopularitySnapshotPort popularitySnapshotPort;
    private final CommunitySignalsPort communitySignalsPort;
    private final EventCatalogPort eventCatalogPort;
    private final DemandSignalRepository demandSignalRepository;
    private final ArtistDiscoveryProfileRepository profileRepository;

    @Transactional
    public ArtistDiscoveryProfile execute(ArtistId artistId) {
        ArtistCatalogInfo catalogInfo = artistCatalogPort.findById(artistId)
                .orElseThrow(() -> new ArtistDiscoveryProfileNotFoundException(
                        "Artist not found in catalog: " + artistId));

        Optional<ArtistDiscoveryProfile> existing = profileRepository.findByArtistId(artistId);

        Integer popularity = popularitySnapshotPort.findLatestPopularity(artistId).orElse(null);
        CommunitySignals community = communitySignalsPort.findSignals(artistId);
        Optional<UpcomingEventInfo> nextEvent = eventCatalogPort.findNextUpcomingEvent(artistId);

        Instant now = Instant.now();
        Instant recentSince = now.minus(MOMENTUM_WINDOW_DAYS, ChronoUnit.DAYS);
        Instant previousSince = now.minus(2L * MOMENTUM_WINDOW_DAYS, ChronoUnit.DAYS);
        long recentSignals = demandSignalRepository.countByArtistAndPeriod(artistId, recentSince, now);
        long previousSignals = demandSignalRepository.countByArtistAndPeriod(artistId, previousSince, recentSince);
        long totalDemand = demandSignalRepository.countByArtist(artistId);

        DiscoveryProfileSignals signals = new DiscoveryProfileSignals(
                popularity,
                community.reviewCount(),
                totalDemand,
                community.verifiedAttendeesCount(),
                community.knownByCount(),
                recentSignals,
                previousSignals,
                nextEvent.isPresent(),
                nextEvent.map(e -> e.eventDate().atZone(java.time.ZoneOffset.UTC).toLocalDate()).orElse(null),
                nextEvent.map(UpcomingEventInfo::city).orElse(null),
                catalogInfo.createdAt(),
                existing.map(ArtistDiscoveryProfile::getMusicBrainzId).orElse(null),
                existing.map(ArtistDiscoveryProfile::getMbCountry).orElse(null),
                existing.map(ArtistDiscoveryProfile::getMbCity).orElse(null),
                existing.map(ArtistDiscoveryProfile::getMbBeginYear).orElse(null)
        );

        ArtistDiscoveryProfile profile = ArtistDiscoveryProfile.calculate(artistId, signals);
        return profileRepository.save(profile);
    }
}
