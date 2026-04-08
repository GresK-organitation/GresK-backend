package com.gresk.modules.event.domain.model;

import java.util.Objects;

public record Location(String city, String address, String venue) {

    public Location {
        Objects.requireNonNull(city, "Address city must not be null");
        Objects.requireNonNull(address, "Address address must not be null");
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Address city must not be blank");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address address must not be blank");
        }
    }
}
