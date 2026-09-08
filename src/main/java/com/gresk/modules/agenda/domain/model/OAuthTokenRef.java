package com.gresk.modules.agenda.domain.model;

import java.time.Instant;

/**
 * Referencia opaca a las credenciales OAuth de una {@link CalendarSyncAccount}.
 * El dominio solo maneja el texto cifrado — nunca ve el access/refresh token en claro;
 * el cifrado/descifrado ocurre exclusivamente en infraestructura
 * ({@code CalendarTokenEncryptionService}).
 */
public record OAuthTokenRef(String accessTokenCiphertext, String refreshTokenCiphertext, Instant tokenExpiry) {

    public static OAuthTokenRef of(String accessTokenCiphertext, String refreshTokenCiphertext, Instant tokenExpiry) {
        return new OAuthTokenRef(accessTokenCiphertext, refreshTokenCiphertext, tokenExpiry);
    }
}
