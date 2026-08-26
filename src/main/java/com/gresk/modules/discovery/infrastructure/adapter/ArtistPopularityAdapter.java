package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.artist.domain.model.ArtistMetricsSnapshot;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistMetricsSnapshotRepositoryPort;
import com.gresk.modules.discovery.domain.port.out.ArtistPopularitySnapshotPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Lee la última popularidad de Spotify ya sincronizada por el módulo
 * `artist` (ArtistMetricsSnapshotScheduler, cada 3 días). Discovery nunca
 * llama a la API de Spotify directamente.
 */
@Component
@RequiredArgsConstructor
public class ArtistPopularityAdapter implements ArtistPopularitySnapshotPort {

    private final ArtistMetricsSnapshotRepositoryPort snapshotRepositoryPort;

    @Override
    public Optional<Integer> findLatestPopularity(ArtistId artistId) {
        return snapshotRepositoryPort.findLatestByArtistId(artistId)
                .map(ArtistMetricsSnapshot::spotifyPopularity);
    }
}
