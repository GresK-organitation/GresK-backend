package com.gresk.shared.domain.valueobject;

import com.gresk.shared.domain.exception.InvalidNameException;

public record Name(String value) {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    public Name {
        if (value == null || value.isBlank()) {
            throw new InvalidNameException("Name cannot be null or blank");
        }

        value = value.trim();

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new InvalidNameException(
                    String.format("Name should have between %d and %d characters", MIN_LENGTH, MAX_LENGTH)
            );
        }
    }

    @Override
    public String toString() {
        return value;
    }
}