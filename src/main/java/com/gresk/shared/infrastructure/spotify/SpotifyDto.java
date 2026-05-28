package com.gresk.shared.infrastructure.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * DTOs que mapean las respuestas de la Spotify Web API.
 * Agrupados en una sola clase porque son contratos externos (no dominio).
 */
public class SpotifyDto {

    // ── Token ────────────────────────────────────────────────────────────────

    public record SpotifyTokenResponse(
            @JsonProperty("access_token") String accessToken
    ) {}

    // ── Track search ─────────────────────────────────────────────────────────

    public record SpotifySearchResponse(
            SpotifyTracksWrapper tracks
    ) {}

    public record SpotifyTracksWrapper(
            List<SpotifyTrackDTO> items
    ) {}

    public record SpotifyTrackDTO(
            String name,
            int popularity,
            @JsonProperty("external_urls") Map<String, String> externalUrls,
            List<SpotifyArtistDTO> artists,
            SpotifyAlbumDTO album,
            @JsonProperty("preview_url") String previewUrl
    ) {}

    public record SpotifyAlbumDTO(
            String name,
            List<SpotifyImageDTO> images
    ) {}

    // ── Artist search ─────────────────────────────────────────────────────────

    public record SpotifyArtistSearchResponse(
            SpotifyArtistsWrapper artists
    ) {}

    public record SpotifyArtistsWrapper(
            List<SpotifyArtistResultDTO> items
    ) {}

    public record SpotifyArtistResultDTO(
            String id,
            String name,
            List<String> genres,
            SpotifyFollowersDTO followers,
            List<SpotifyImageDTO> images,
            int popularity,
            @JsonProperty("external_urls") Map<String, String> externalUrls
    ) {}

    public record SpotifyFollowersDTO(
            int total
    ) {}

    // ── Artist detail (GET /artists/{id}) ────────────────────────────────────

    public record ArtistDetailResponse(
            int                  popularity,
            SpotifyFollowersDTO  followers,
            List<String>         genres,
            String               name,
            List<SpotifyImageDTO> images
    ) {}

    // ── Artist albums (GET /artists/{id}/albums) ──────────────────────────────

    public record AlbumSearchResponse(
            List<AlbumItemDTO> items,
            int                total
    ) {}

    public record AlbumItemDTO(
            String id,
            String name,
            @JsonProperty("release_date")           String releaseDate,
            @JsonProperty("release_date_precision")  String releaseDatePrecision
    ) {}

    // ── Shared ────────────────────────────────────────────────────────────────

    public record SpotifyArtistDTO(
            String name,
            String id,
            @JsonProperty("external_urls") Map<String, String> externalUrls
    ) {}

    public record SpotifyImageDTO(
            String url,
            int height,
            int width
    ) {}
}
