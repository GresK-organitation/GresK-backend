package com.gresk.modules.promoter.domain.valueObjects;

import java.util.Objects;

public final class Password {
    private final String hashedValue;

    public Password(String hashedValue) {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new IllegalArgumentException("La contraseña hasheada no puede estar vacía");
        }
        this.hashedValue = hashedValue;
    }

    public String hashedValue() { return hashedValue; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password password)) return false;
        return Objects.equals(hashedValue, password.hashedValue);
    }

    @Override
    public int hashCode() { return Objects.hash(hashedValue); }

    @Override
    public String toString() { return "[PROTECTED]"; }
}