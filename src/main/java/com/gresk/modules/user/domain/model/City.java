package com.gresk.modules.user.domain.model;

import com.gresk.modules.user.domain.exception.InvalidCityException;

import java.util.Objects;

public record City(String value) {

    public City {
        Objects.requireNonNull(value, "City value cannot be null");
        value = value.trim();
        if (value.isBlank()) {
            throw new InvalidCityException("City cannot be blank");
        }
    }

    public static City of(String value) {
        return new City(value);
    }

    public static City from(String value) {
        return new City(value);
    }

    public boolean isSameAs(City other) {
        return other != null && this.value.equalsIgnoreCase(other.value());
    }

    @Override
    public String toString() {
        return value;
    }
}