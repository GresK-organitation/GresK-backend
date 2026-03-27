package com.gresk.modules.promoter.valueObjects;

import com.gresk.modules.promoter.exception.InvalidPromoterNameException;

import java.util.Objects;

public final class PromoterName {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    private final String value;

    public PromoterName(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidPromoterNameException("El nombre no puede estar vacío");
        }
        String cleanedValue = value.trim();

        if (cleanedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("El nombre debe tener entre %d y %d caracteres", MIN_LENGTH, MAX_LENGTH)
            );
        }

        this.value = cleanedValue;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromoterName name)) return false;
        return Objects.equals(value, name.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

}