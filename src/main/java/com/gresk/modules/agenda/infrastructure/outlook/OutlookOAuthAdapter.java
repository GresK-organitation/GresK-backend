package com.gresk.modules.agenda.infrastructure.outlook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;
import com.gresk.modules.agenda.domain.port.out.OAuthCalendarClientPort;
import com.gresk.modules.agenda.infrastructure.security.CalendarTokenEncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Flujo OAuth2 (authorization-code) de Microsoft Graph para Outlook Calendar. Implementado
 * con llamadas REST planas ({@link HttpClient} + Jackson) en vez del SDK {@code microsoft-graph}
 * — evita una dependencia pesada solo para el intercambio de tokens, que es un simple POST
 * form-urlencoded contra el endpoint v2.0 de Azure AD.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutlookOAuthAdapter implements OAuthCalendarClientPort {

    private static final String SCOPES = "offline_access Calendars.ReadWrite User.Read";

    private final OutlookProperties properties;
    private final CalendarTokenEncryptionService encryptionService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public CalendarProvider supportedProvider() {
        return CalendarProvider.OUTLOOK;
    }

    @Override
    public String buildAuthorizationUrl(String state) {
        Map<String, String> params = Map.of(
                "client_id", properties.clientId(),
                "response_type", "code",
                "redirect_uri", properties.redirectUri(),
                "response_mode", "query",
                "scope", SCOPES,
                "state", state);
        return properties.authorizeEndpoint() + "?" + toQueryString(params);
    }

    @Override
    public ExternalAccountAuthResult exchangeAuthorizationCode(String authorizationCode) {
        Map<String, String> form = Map.of(
                "client_id", properties.clientId(),
                "client_secret", properties.clientSecret(),
                "grant_type", "authorization_code",
                "code", authorizationCode,
                "redirect_uri", properties.redirectUri(),
                "scope", SCOPES);
        JsonNode tokenResponse = postForm(properties.tokenEndpoint(), form);

        OAuthTokenRef tokenRef = toTokenRef(tokenResponse);
        String email = resolveAccountEmail(tokenResponse.get("access_token").asText());
        return new ExternalAccountAuthResult(tokenRef, email);
    }

    @Override
    public OAuthTokenRef refreshIfNeeded(OAuthTokenRef tokenRef) {
        boolean expired = tokenRef.tokenExpiry() == null || !tokenRef.tokenExpiry().isAfter(Instant.now().plusSeconds(60));
        if (!expired) {
            return tokenRef;
        }
        String refreshToken = encryptionService.decrypt(tokenRef.refreshTokenCiphertext());
        Map<String, String> form = Map.of(
                "client_id", properties.clientId(),
                "client_secret", properties.clientSecret(),
                "grant_type", "refresh_token",
                "refresh_token", refreshToken,
                "scope", SCOPES);
        JsonNode tokenResponse = postForm(properties.tokenEndpoint(), form);
        return toTokenRef(tokenResponse);
    }

    @Override
    public void revoke(OAuthTokenRef tokenRef) {
        // Microsoft Graph no expone un endpoint público de revocación puntual de un refresh
        // token individual (a diferencia de Google) — la desconexión efectiva se hace
        // olvidando el token en nuestro lado; el token expira o el usuario puede revocar
        // el consentimiento desde https://myaccount.microsoft.com/.
        log.info("Outlook token forgotten locally (Microsoft Graph has no single-token revoke endpoint)");
    }

    private OAuthTokenRef toTokenRef(JsonNode tokenResponse) {
        Instant expiry = tokenResponse.hasNonNull("expires_in")
                ? Instant.now().plusSeconds(tokenResponse.get("expires_in").asLong()) : null;
        return new OAuthTokenRef(
                encryptionService.encrypt(tokenResponse.get("access_token").asText()),
                encryptionService.encrypt(tokenResponse.get("refresh_token").asText()),
                expiry);
    }

    private String resolveAccountEmail(String accessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://graph.microsoft.com/v1.0/me"))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(response.body());
            if (node.hasNonNull("mail")) return node.get("mail").asText();
            return node.hasNonNull("userPrincipalName") ? node.get("userPrincipalName").asText() : null;
        } catch (Exception e) {
            log.warn("Could not resolve Outlook account email: {}", e.getMessage());
            return null;
        }
    }

    private JsonNode postForm(String url, Map<String, String> form) {
        try {
            String body = toQueryString(form);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(response.body());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Microsoft Graph token request failed: " + response.body());
            }
            return node;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Microsoft Graph token request failed", e);
        }
    }

    private String toQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
