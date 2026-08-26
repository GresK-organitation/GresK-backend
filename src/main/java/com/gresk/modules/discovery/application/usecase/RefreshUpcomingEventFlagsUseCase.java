package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogPort;
import com.gresk.modules.discovery.domain.port.out.ArtistDiscoveryProfileRepository;
import com.gresk.modules.discovery.domain.port.out.EventCatalogPort;
import com.gresk.modules.discovery.domain.port.out.UpcomingEventInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Job diario ligero: refresca solo has_upcoming_events / next_event_date /
 * next_event_city sin recalcular el GresK Score completo (eso lo hace el
 * job semanal). Solo actualiza artistas que ya tienen perfil calculado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshUpcomingEventFlagsUseCase {

    private final ArtistCatalogPort artistCatalogPort;
    private final ArtistDiscoveryProfileRepository profileRepository;
    private final EventCatalogPort eventCatalogPort;

    public void execute() {
        for (ArtistId artistId : artistCatalogPort.findAllArtistIds()) {
            try {
                refreshOne(artistId);
            } catch (Exception e) {
                log.error("Failed to refresh upcoming event flags for artist {}: {}", artistId, e.getMessage());
            }
        }
    }

    @Transactional
    void refreshOne(ArtistId artistId) {
        Optional<ArtistDiscoveryProfile> existing = profileRepository.findByArtistId(artistId);
        if (existing.isEmpty()) return;

        Optional<UpcomingEventInfo> nextEvent = eventCatalogPort.findNextUpcomingEvent(artistId);
        ArtistDiscoveryProfile updated = existing.get().withUpcomingEventFlags(
                nextEvent.isPresent(),
                nextEvent.map(e -> e.eventDate().atZone(ZoneOffset.UTC).toLocalDate()).orElse(null),
                nextEvent.map(UpcomingEventInfo::city).orElse(null)
        );
        profileRepository.save(updated);
    }
}
