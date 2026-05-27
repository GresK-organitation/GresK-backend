package com.gresk.modules.artist.application.dto;

import java.util.List;

/**
 * DTO de la capa de aplicación que representa una sugerencia de artista
 * devuelta por la búsqueda en Spotify.
 *
 * Viaja desde el adaptador de infraestructura hasta el controlador
 * sin depender de la capa web ni de la infraestructura de Spotify.
 */
public record SpotifyArtistSuggestionDTO(
        String       spotifyArtistId,
        String       name,
        String       imageUrl,
        List<String> genres,
        int          popularity,
        int          followers
) {}
