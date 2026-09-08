package com.gresk.modules.agenda.infrastructure.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.gresk.modules.agenda.domain.model.OAuthTokenRef;
import com.gresk.modules.agenda.infrastructure.security.CalendarTokenEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/** Construye credenciales/cliente de Google Calendar a partir de un {@link OAuthTokenRef} cifrado. */
@Component
@RequiredArgsConstructor
class GoogleCredentialsFactory {

    private final GoogleCalendarProperties properties;
    private final CalendarTokenEncryptionService encryptionService;

    UserCredentials credentialsFor(OAuthTokenRef tokenRef) {
        String accessToken = tokenRef.accessTokenCiphertext() == null ? null
                : encryptionService.decrypt(tokenRef.accessTokenCiphertext());
        String refreshToken = encryptionService.decrypt(tokenRef.refreshTokenCiphertext());

        UserCredentials.Builder builder = UserCredentials.newBuilder()
                .setClientId(properties.clientId())
                .setClientSecret(properties.clientSecret())
                .setRefreshToken(refreshToken);
        if (accessToken != null) {
            builder.setAccessToken(new AccessToken(accessToken,
                    tokenRef.tokenExpiry() != null ? Date.from(tokenRef.tokenExpiry()) : null));
        }
        return builder.build();
    }

    Calendar clientFor(OAuthTokenRef tokenRef) {
        try {
            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentialsFor(tokenRef)))
                    .setApplicationName("GresK")
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build Google Calendar client", e);
        }
    }
}
