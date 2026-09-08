package com.gresk.modules.agenda.infrastructure.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.agenda.domain.model.CalendarProvider;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;
import com.gresk.modules.agenda.domain.port.out.OAuthCalendarClientPort;
import com.gresk.modules.agenda.infrastructure.security.CalendarTokenEncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

/**
 * Flujo OAuth2 de Google Calendar — mismo patrón que {@code email.infrastructure.gmail.GmailOAuthService},
 * usando {@code google-api-client}/{@code google-auth-library-oauth2-http}. Scope de solo
 * lectura+escritura del calendario del usuario (no acceso completo a la cuenta Google).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleCalendarOAuthAdapter implements OAuthCalendarClientPort {

    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_EVENTS, "email");

    private final GoogleCalendarProperties properties;
    private final GoogleCredentialsFactory credentialsFactory;
    private final CalendarTokenEncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    @Override
    public CalendarProvider supportedProvider() {
        return CalendarProvider.GOOGLE;
    }

    @Override
    public String buildAuthorizationUrl(String state) {
        return new GoogleAuthorizationCodeRequestUrl(properties.clientId(), properties.redirectUri(), SCOPES)
                .setState(state)
                .setAccessType("offline")
                .set("prompt", "consent")
                .build();
    }

    @Override
    public ExternalAccountAuthResult exchangeAuthorizationCode(String authorizationCode) {
        try {
            GoogleTokenResponse tokens = new GoogleAuthorizationCodeTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    properties.clientId(), properties.clientSecret(),
                    authorizationCode, properties.redirectUri())
                    .execute();

            Instant expiry = tokens.getExpiresInSeconds() != null
                    ? Instant.now().plusSeconds(tokens.getExpiresInSeconds()) : null;

            OAuthTokenRef tokenRef = new OAuthTokenRef(
                    encryptionService.encrypt(tokens.getAccessToken()),
                    encryptionService.encrypt(tokens.getRefreshToken()),
                    expiry);

            String email = resolveAccountEmail(tokens.getAccessToken());
            return new ExternalAccountAuthResult(tokenRef, email);
        } catch (Exception e) {
            throw new IllegalStateException("Google Calendar OAuth callback failed", e);
        }
    }

    @Override
    public OAuthTokenRef refreshIfNeeded(OAuthTokenRef tokenRef) {
        boolean expired = tokenRef.tokenExpiry() == null || !tokenRef.tokenExpiry().isAfter(Instant.now().plusSeconds(60));
        if (!expired) {
            return tokenRef;
        }
        try {
            UserCredentials credentials = credentialsFactory.credentialsFor(tokenRef);
            credentials.refresh();
            AccessToken newAccessToken = credentials.getAccessToken();
            Instant newExpiry = newAccessToken.getExpirationTime() != null
                    ? newAccessToken.getExpirationTime().toInstant() : null;
            return new OAuthTokenRef(encryptionService.encrypt(newAccessToken.getTokenValue()),
                    tokenRef.refreshTokenCiphertext(), newExpiry);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot refresh Google Calendar token", e);
        }
    }

    @Override
    public void revoke(OAuthTokenRef tokenRef) {
        try {
            String refreshToken = encryptionService.decrypt(tokenRef.refreshTokenCiphertext());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/revoke?token=" + refreshToken))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            log.warn("Could not revoke Google Calendar token: {}", e.getMessage());
        }
    }

    private String resolveAccountEmail(String accessToken) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var node = objectMapper.readTree(response.body());
            return node.hasNonNull("email") ? node.get("email").asText() : null;
        } catch (Exception e) {
            log.warn("Could not resolve Google account email: {}", e.getMessage());
            return null;
        }
    }
}
