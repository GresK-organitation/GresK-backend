package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.model.DemandSignal;
import com.gresk.modules.user.domain.model.UserId;

import java.time.Instant;
import java.util.Optional;

public interface DemandSignalRepository {
    DemandSignal save(DemandSignal signal);
    Optional<DemandSignal> findByArtistAndUser(ArtistId artistId, UserId userId);
    void delete(DemandSignal signal);
    long countByArtist(ArtistId artistId);
    long countByArtistAndPeriod(ArtistId artistId, Instant from, Instant to);
}
