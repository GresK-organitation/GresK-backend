package com.gresk.shared.domain.valueobject;

import com.gresk.shared.domain.exception.InvalidPasswordException;

public record Password(String hashedValue) {

    public Password {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new InvalidPasswordException("Hashed password cannot be empty");
        }

        if (isPlaintext(hashedValue)) {
            throw new InvalidPasswordException("Security breach: Raw password passed to Password VO");
        }
    }

    private boolean isPlaintext(String value) {
        return value.length() < 20;
    }

    @Override
    public String toString() {
        return "********";
    }
}