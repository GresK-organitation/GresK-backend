package com.gresk.modules.artist.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Registro histórico de métricas de Spotify para un artista en una fecha concreta.
 * No tiene lógica de negocio: es un artefacto de lectura/analítica.
 */
public record ArtistMetricsSnapshot(
        UUID      id,
        ArtistId  artistId,
        LocalDate snapshotDate,
        Integer   spotifyPopularity,
        Integer   spotifyFollowers,
        LocalDate lastReleaseDate,
        Integer   totalReleases,
        Instant   createdAt
) {
    public static ArtistMetricsSnapshot create(
            ArtistId  artistId,
            Integer   popularity,
            Integer   followers,
            LocalDate lastReleaseDate,
            Integer   totalReleases) {
        return new ArtistMetricsSnapshot(
                UUID.randomUUID(),
                artistId,
                LocalDate.now(),
                popularity,
                followers,
                lastReleaseDate,
                totalReleases,
                Instant.now()
        );
    }
}
