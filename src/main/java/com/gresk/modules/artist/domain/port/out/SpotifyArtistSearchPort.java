package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.application.dto.SpotifyArtistSuggestionDTO;

import java.util.List;

/**
 * Puerto de salida del dominio del artista hacia la infraestructura de Spotify.
 * Permite buscar artistas en Spotify por nombre y obtener sugerencias para
 * que el promotor pueda vincular su artista con el perfil correcto.
 */
public interface SpotifyArtistSearchPort {

    List<SpotifyArtistSuggestionDTO> searchByName(String name, int limit);
}
