package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class SpotifyDto {

    public record SpotifyTokenResponse(
            @JsonProperty("access_token") String accessToken
    ) {}

    public record SpotifySearchResponse(
            SpotifyTracksWrapper tracks
    ) {}

    public record SpotifyTracksWrapper(
            List<SpotifyTrackDTO> items
    ) {}

    public record SpotifyTrackDTO(
            String name,
            int popularity, // 2. IMPORTANTE: Para filtrar emergentes (< 30)
            @JsonProperty("external_urls") Map<String, String> externalUrls,
            List<SpotifyArtistDTO> artists,
            SpotifyAlbumDTO album,
            @JsonProperty("preview_url") String previewUrl // Útil para tu App
    ) {}

    public record SpotifyArtistDTO(
            String name,
            String id,
            @JsonProperty("external_urls") Map<String, String> externalUrls
    ) {}

    public record SpotifyAlbumDTO(
            String name,
            List<SpotifyImageDTO> images
    ) {}

    public record SpotifyImageDTO(
            String url,
            int height,
            int width
    ) {}
}