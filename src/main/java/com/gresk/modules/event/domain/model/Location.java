package com.gresk.modules.event.domain.model;

import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.Coordinates;

import java.util.Objects;

public record Location(Address address, Coordinates coordinates, String venue) {

    public Location {
        Objects.requireNonNull(address, "Location address must not be null");
        Objects.requireNonNull(coordinates, "Location coordinates must not be null");
        // venue es opcional (puede ser null)
    }
}
