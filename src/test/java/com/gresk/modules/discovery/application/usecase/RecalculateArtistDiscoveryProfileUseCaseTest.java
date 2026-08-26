package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogInfo;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogPort;
import com.gresk.modules.discovery.domain.port.out.ArtistDiscoveryProfileRepository;
import com.gresk.modules.discovery.domain.port.out.ArtistPopularitySnapshotPort;
import com.gresk.modules.discovery.domain.port.out.CommunitySignals;
import com.gresk.modules.discovery.domain.port.out.CommunitySignalsPort;
import com.gresk.modules.discovery.domain.port.out.DemandSignalRepository;
import com.gresk.modules.discovery.domain.port.out.EventCatalogPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecalculateArtistDiscoveryProfileUseCaseTest {

    @Mock private ArtistCatalogPort artistCatalogPort;
    @Mock private ArtistPopularitySnapshotPort popularitySnapshotPort;
    @Mock private CommunitySignalsPort communitySignalsPort;
    @Mock private EventCatalogPort eventCatalogPort;
    @Mock private DemandSignalRepository demandSignalRepository;
    @Mock private ArtistDiscoveryProfileRepository profileRepository;

    private RecalculateArtistDiscoveryProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RecalculateArtistDiscoveryProfileUseCase(artistCatalogPort, popularitySnapshotPort,
                communitySignalsPort, eventCatalogPort, demandSignalRepository, profileRepository);
    }

    @Test
    void execute_ensamblaSenalesDeTodosLosPortsYPersisteElPerfil() {
        ArtistId artistId = ArtistId.generate();
        ArtistCatalogInfo catalogInfo = new ArtistCatalogInfo(
                artistId.toString(), "Núcleo Duro", "Barcelona", List.of("ROCK"), "img", "bio",
                4.7, null, null, null, Instant.now().minusSeconds(1000));

        when(artistCatalogPort.findById(artistId)).thenReturn(Optional.of(catalogInfo));
        when(profileRepository.findByArtistId(artistId)).thenReturn(Optional.empty());
        when(popularitySnapshotPort.findLatestPopularity(artistId)).thenReturn(Optional.of(10));
        when(communitySignalsPort.findSignals(artistId)).thenReturn(new CommunitySignals(8, 6, 52));
        when(eventCatalogPort.findNextUpcomingEvent(artistId)).thenReturn(Optional.empty());
        when(demandSignalRepository.countByArtistAndPeriod(any(), any(), any())).thenReturn(2L);
        when(demandSignalRepository.countByArtist(artistId)).thenReturn(5L);
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArtistDiscoveryProfile result = useCase.execute(artistId);

        assertEquals(artistId, result.getArtistId());
        assertEquals(8, result.getGreskReviewCount());
        assertEquals(52, result.getKnownByCount());
        verify(profileRepository).save(any(ArtistDiscoveryProfile.class));
    }
}
