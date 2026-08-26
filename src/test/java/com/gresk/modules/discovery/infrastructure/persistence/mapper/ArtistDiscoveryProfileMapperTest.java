package com.gresk.modules.discovery.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.ArtistDiscoveryProfile;
import com.gresk.modules.discovery.domain.port.out.DiscoveryProfileSignals;
import com.gresk.modules.discovery.infrastructure.persistence.entity.ArtistDiscoveryProfileEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArtistDiscoveryProfileMapperTest {

    private final ArtistDiscoveryProfileMapper mapper = new ArtistDiscoveryProfileMapper();

    @Test
    void toEntityYVueltaADominioPreservaTodosLosCampos() {
        ArtistId artistId = ArtistId.generate();
        DiscoveryProfileSignals signals = new DiscoveryProfileSignals(
                20, 5, 3, 2, 8, 2, 1, true, LocalDate.now().plusDays(3), "Barcelona",
                Instant.now().minusSeconds(1000), "mb-123", "ES", "Barcelona", 2015);
        ArtistDiscoveryProfile original = ArtistDiscoveryProfile.calculate(artistId, signals);

        ArtistDiscoveryProfileEntity entity = mapper.toEntity(original);
        ArtistDiscoveryProfile roundTripped = mapper.toDomain(entity);

        assertEquals(original.getArtistId(), roundTripped.getArtistId());
        assertEquals(original.getSizeTier(), roundTripped.getSizeTier());
        assertEquals(original.getGreskScore(), roundTripped.getGreskScore());
        assertEquals(original.getMusicBrainzId(), roundTripped.getMusicBrainzId());
        assertEquals(original.isHasUpcomingEvents(), roundTripped.isHasUpcomingEvents());
        assertEquals(original.getKnownByCount(), roundTripped.getKnownByCount());
    }
}
