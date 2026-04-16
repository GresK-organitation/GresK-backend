package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class SpotifyDto {
    // Respuesta del Token
    public record SpotifyTokenResponse(
            @JsonProperty("access_token") String accessToken
    ) {}

    // Respuesta de Recomendaciones
    public record SpotifyRecommendationsResponse(List<SpotifyTrackDTO> tracks) {}

    public record SpotifyTrackDTO(
            String name,
            @JsonProperty("external_urls") Map<String, String> externalUrls,
            List<SpotifyArtistDTO> artists,
            SpotifyAlbumDTO album
    ) {}

    public record SpotifyArtistDTO(String name) {}

    public record SpotifyAlbumDTO(List<SpotifyImageDTO> images) {}

    public record SpotifyImageDTO(String url) {}
}
