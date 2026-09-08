package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.ArtistTractionSnapshot;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.time.LocalDate;
import java.util.Optional;

public interface ArtistTractionSnapshotRepositoryPort {
    void save(ArtistTractionSnapshot snapshot);
    void deleteOlderThan(LocalDate cutoff);
    Optional<ArtistTractionSnapshot> findLatestByArtistId(ArtistId artistId);
}
