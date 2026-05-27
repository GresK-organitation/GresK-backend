package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.dto.SpotifyArtistSuggestionDTO;

import java.util.List;

/**
 * Puerto de entrada para la búsqueda de artistas en Spotify.
 * Lo invoca el controlador REST cuando el promotor escribe el nombre
 * del artista que quiere vincular.
 */
public interface SearchSpotifyArtistsPort {

    List<SpotifyArtistSuggestionDTO> execute(String name);
}
