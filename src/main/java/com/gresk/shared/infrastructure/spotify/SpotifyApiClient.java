package com.gresk.shared.infrastructure.spotify;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "spotify-api", url = "${spotify.api-url}")
public interface SpotifyApiClient {

    /**
     * Busca tracks en Spotify. Usado para las recomendaciones del dashboard.
     */
    @GetMapping("/search")
    SpotifyDto.SpotifySearchResponse searchTracks(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("q") String query,
            @RequestParam("type") String type,
            @RequestParam("market") String market,
            @RequestParam("limit") int limit
    );

    /**
     * Busca artistas en Spotify por nombre. Usado en el formulario de creación
     * de artistas para vincular el perfil GresK con el ID de Spotify.
     */
    @GetMapping("/search")
    SpotifyDto.SpotifyArtistSearchResponse searchArtists(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("q") String query,
            @RequestParam("type") String type,
            @RequestParam("limit") int limit
    );

    /**
     * Obtiene el detalle de un artista (popularidad, seguidores, géneros).
     * Usado por el job de métricas para recopilar datos periódicos.
     */
    @GetMapping("/artists/{id}")
    SpotifyDto.ArtistDetailResponse getArtistDetail(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable("id") String artistId
    );

    /**
     * Obtiene los últimos lanzamientos de un artista.
     * Usado por el job de métricas para registrar fecha del último lanzamiento.
     */
    @GetMapping("/artists/{id}/albums")
    SpotifyDto.AlbumSearchResponse getLatestAlbums(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable("id") String artistId,
            @RequestParam("limit") int limit,
            @RequestParam("include_groups") String includeGroups
    );
}
