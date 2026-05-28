package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.ArtistMetricsSnapshot;

import java.time.LocalDate;

public interface ArtistMetricsSnapshotRepositoryPort {

    void save(ArtistMetricsSnapshot snapshot);

    void deleteOlderThan(LocalDate cutoff);
}
