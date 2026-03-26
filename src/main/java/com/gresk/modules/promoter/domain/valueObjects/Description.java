package com.gresk.modules.promoter.domain.valueObjects;

import java.util.Objects;

public final class Description {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 600;

    public final String value;

    public Description (String value){
        if (value == null || value.isBlank()) {
            this.value = "No description";
            return;
        }

        String cleanedValue = value.trim();

        if (cleanedValue.length() < MIN_LENGTH || cleanedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Description must have between %d and %d characters", MIN_LENGTH, MAX_LENGTH)
            );
        }
        this.value = cleanedValue;
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Description that)) return false;
        return Objects.equals(value, that.value);
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
