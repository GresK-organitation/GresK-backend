package com.gresk.modules.artist.application.dto;

import java.time.LocalDate;

/**
 * Datos de métricas obtenidos de la Spotify Web API para un artista vinculado.
 */
public record SpotifyArtistMetricsDTO(
        int       popularity,
        int       followers,
        LocalDate lastReleaseDate,
        int       totalReleases
) {}
