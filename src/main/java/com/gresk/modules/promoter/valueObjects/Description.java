package com.gresk.modules.promoter.valueObjects;

import java.util.Objects;

public final class Description {

    private static final int MAX_LENGTH = 600;

    public final String value;

    public Description (String value){
        if (value == null || value.isBlank()) {
            this.value =  null;
            return;
        }

        String cleanedValue = value.trim();

        if (cleanedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Description has a limit of %d characters", MAX_LENGTH)
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
        return value != null ? value : ""; }

}
