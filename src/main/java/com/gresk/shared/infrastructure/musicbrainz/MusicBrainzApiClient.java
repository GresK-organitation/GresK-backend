package com.gresk.shared.infrastructure.musicbrainz;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente de MusicBrainz — API pública sin autenticación, pero exige un
 * User-Agent identificable y respeta 1 req/s (ver resilience4j.musicbrainz).
 */
@FeignClient(name = "musicbrainz-api", url = "${musicbrainz.api-url}")
public interface MusicBrainzApiClient {

    @GetMapping("/artist")
    MusicBrainzDto.ArtistSearchResponse searchArtists(
            @RequestHeader("User-Agent") String userAgent,
            @RequestParam("query") String query,
            @RequestParam("fmt") String format,
            @RequestParam("limit") int limit
    );
}
