package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.util.Optional;

/**
 * Lee la última popularidad de Spotify ya sincronizada por
 * {@code ArtistMetricsSnapshotScheduler} (cada 3 días). Discovery nunca
 * llama a la API de Spotify directamente — reutiliza el snapshot existente
 * para no duplicar peticiones contra el rate limit.
 */
public interface ArtistPopularitySnapshotPort {
    Optional<Integer> findLatestPopularity(ArtistId artistId);
}
