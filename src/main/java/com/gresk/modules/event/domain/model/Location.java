package com.gresk.modules.event.domain.model;

import java.util.Objects;

public record Location(String city, String address, String venue) {

    public Location {
        Objects.requireNonNull(city, "Location city must not be null");
        Objects.requireNonNull(address, "Location address must not be null");
        if (city.isBlank()) {
            throw new IllegalArgumentException("Location city must not be blank");
        }
        if (address.isBlank()) {
            throw new IllegalArgumentException("Location address must not be blank");
        }
    }
}
