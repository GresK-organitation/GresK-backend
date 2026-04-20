package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "spotify-auth", url = "${spotify.auth-url}")
public interface SpotifyAuthClient {

    @PostMapping(value = "", consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    SpotifyDto.SpotifyTokenResponse getToken(
            @RequestHeader("Authorization") String basicAuth,
            @RequestBody Map<String, ?> body);
}