package com.gresk.shared.domain.valueobject;

import com.gresk.shared.domain.exception.InvalidLocationException;

import java.util.Optional;

public record Location(
        String city,
        String venue,
        String address
) {
    public Location {
        if (city == null || city.isBlank()) throw new InvalidLocationException("City is required");
        if (venue == null || venue.isBlank()) throw new InvalidLocationException("Venue is required");

        city = city.trim();
        venue = venue.trim();
        address = (address == null || address.isBlank()) ? null : address.trim();
    }

    public Optional<String> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public String fullLocation() {
        return address != null
                ? String.format("%s, %s (%s)", venue, city, address)
                : String.format("%s, %s", venue, city);
    }
}