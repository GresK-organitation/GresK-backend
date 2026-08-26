package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.ArtistMetricsSnapshot;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.time.LocalDate;
import java.util.Optional;

public interface ArtistMetricsSnapshotRepositoryPort {

    void save(ArtistMetricsSnapshot snapshot);

    void deleteOlderThan(LocalDate cutoff);

    Optional<ArtistMetricsSnapshot> findLatestByArtistId(ArtistId artistId);
}
