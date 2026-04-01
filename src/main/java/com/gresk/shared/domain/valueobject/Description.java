package com.gresk.shared.domain.valueobject;

import com.gresk.shared.domain.exception.InvalidDescriptionException;

import java.util.Optional;

public record Description(String value) {

    private static final int MAX_LENGTH = 600;

    public Description {
        if (value == null || value.isBlank()) {
            value = "";
        } else {
            value = value.trim();
            if (value.length() > MAX_LENGTH) {
                throw new InvalidDescriptionException( String.format("Description exceeds the limit of %d characters", MAX_LENGTH)
                );
            }
        }
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public Optional<String> optionalValue() {
        return isEmpty() ? Optional.empty() : Optional.of(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
