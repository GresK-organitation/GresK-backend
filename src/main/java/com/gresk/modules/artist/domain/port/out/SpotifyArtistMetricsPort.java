package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.application.dto.SpotifyArtistMetricsDTO;

/**
 * Puerto de salida del dominio hacia la infraestructura de Spotify.
 * Obtiene las métricas actuales (popularidad, seguidores, último lanzamiento)
 * de un artista ya vinculado mediante su Spotify Artist ID.
 */
public interface SpotifyArtistMetricsPort {

    SpotifyArtistMetricsDTO fetchMetrics(String spotifyArtistId);
}
