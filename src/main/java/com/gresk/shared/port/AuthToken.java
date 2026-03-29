package com.gresk.shared.port;

public record AuthToken(String token, long expiresIn) {
    public AuthToken {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be blank");
        }
    }
}
