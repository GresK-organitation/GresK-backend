package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "spotify-api", url = "${spotify.api-url}")
public interface SpotifyApiClient {

    @GetMapping("/v1/recommendations")
    SpotifyDto.SpotifyRecommendationsResponse getRecommendations(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("seed_genres") String genres,
            @RequestParam("limit") int limit
    );
}