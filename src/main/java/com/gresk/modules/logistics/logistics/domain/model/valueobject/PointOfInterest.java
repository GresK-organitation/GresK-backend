package com.gresk.modules.logistics.domain.model.valueobject;

import com.gresk.modules.logistics.domain.model.POICategory;
import com.gresk.shared.domain.valueobject.Coordinates;

/**
 * Ubicación de interés para el Tour Book (restaurante concertado, farmacia, hospital...).
 * coordinates es opcional: puede añadirse el pin de GPS más tarde sin bloquear el alta.
 */
public record PointOfInterest(String name, POICategory category, String address, Coordinates coordinates, String notes) {

    public PointOfInterest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("PointOfInterest name must not be blank");
        }
        if (category == null) {
            throw new IllegalArgumentException("PointOfInterest category must not be null");
        }
    }
}
