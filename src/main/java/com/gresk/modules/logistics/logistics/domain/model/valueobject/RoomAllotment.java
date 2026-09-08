package com.gresk.modules.logistics.domain.model.valueobject;

import com.gresk.modules.logistics.domain.model.RoomType;
import com.gresk.shared.domain.valueobject.Money;

/** Cupo contratado de un tipo de habitación en un hotel block, con su coste por noche. */
public record RoomAllotment(RoomType roomType, int quantity, Money costPerNight) {

    public RoomAllotment {
        if (roomType == null) throw new IllegalArgumentException("RoomAllotment roomType must not be null");
        if (quantity < 0) throw new IllegalArgumentException("RoomAllotment quantity cannot be negative");
        if (costPerNight == null) throw new IllegalArgumentException("RoomAllotment costPerNight must not be null");
    }
}
