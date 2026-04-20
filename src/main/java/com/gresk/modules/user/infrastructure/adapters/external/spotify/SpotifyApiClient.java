package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "spotify-api", url = "${spotify.api-url}")
public interface SpotifyApiClient {

    @GetMapping("/search")
    SpotifyDto.SpotifySearchResponse searchEmergingArtists(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("q") String query,
            @RequestParam("type") String type,
            @RequestParam("market") String market,
            @RequestParam("limit") int limit
    );
}