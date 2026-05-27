package com.gresk.shared.infrastructure.spotify;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

/**
 * Servicio compartido para obtener el Bearer token de Spotify
 * usando el flujo Client Credentials (token de máquina, no de usuario).
 *
 * Centraliza la lógica de autenticación para que tanto el módulo user
 * como el módulo artist puedan obtener tokens sin duplicar código.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpotifyTokenService {

    private final SpotifyAuthClient authClient;
    private final SpotifyConfig     config;

    /**
     * Devuelve el header "Bearer {token}" listo para usar en llamadas a la Spotify API.
     * El token tiene validez de 3600 segundos en Spotify.
     */
    public String getBearerToken() {
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString((config.getClientId() + ":" + config.getClientSecret()).getBytes());

        SpotifyDto.SpotifyTokenResponse response =
                authClient.getToken(basicAuth, Map.of("grant_type", "client_credentials"));

        return "Bearer " + response.accessToken();
    }
}
