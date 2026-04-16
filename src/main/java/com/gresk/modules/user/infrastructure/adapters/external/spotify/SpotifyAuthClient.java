package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "spotify-auth", url = "${spotify.auth-url}")
public interface SpotifyAuthClient {

    @PostMapping(value = "/api/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    SpotifyDto.SpotifyTokenResponse getToken(
            @RequestHeader("Authorization") String basicAuth,
            @RequestParam("grant_type") String grantType // <-- Asegúrate de que sea @RequestParam
    );
}