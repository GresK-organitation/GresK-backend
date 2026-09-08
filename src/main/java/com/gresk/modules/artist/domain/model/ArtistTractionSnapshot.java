package com.gresk.modules.artist.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.GeographicTraction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Snapshot histórico de tracción no-Spotify (Bandsintown + geografía agregada).
 * Registro plano, sin lógica de negocio, análogo a ArtistMetricsSnapshot pero
 * separado de él para no acoplar el mapper JPA ya existente de métricas Spotify
 * a una estructura anidada (GeographicTraction).
 */
public record ArtistTractionSnapshot(
        UUID id,
        ArtistId artistId,
        LocalDate snapshotDate,
        Integer bandsintownFollowers,
        Integer bandsintownUpcomingShows,
        GeographicTraction geographicTraction,
        Instant createdAt
) {
    public static ArtistTractionSnapshot create(ArtistId artistId, Integer bandsintownFollowers,
                                                 Integer bandsintownUpcomingShows,
                                                 GeographicTraction geographicTraction) {
        return new ArtistTractionSnapshot(
                UUID.randomUUID(),
                artistId,
                LocalDate.now(),
                bandsintownFollowers,
                bandsintownUpcomingShows,
                geographicTraction != null ? geographicTraction : GeographicTraction.empty(),
                Instant.now()
        );
    }
}
