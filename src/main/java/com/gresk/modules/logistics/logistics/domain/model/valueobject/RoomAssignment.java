package com.gresk.modules.logistics.domain.model.valueobject;

import com.gresk.modules.logistics.domain.model.RoomType;

import java.util.List;
import java.util.UUID;

/** Habitación concreta asignada a uno o varios TravelPartyMember.id. */
public record RoomAssignment(UUID id, RoomType roomType, List<UUID> occupantIds, String roomNumber) {

    public RoomAssignment {
        if (id == null) id = UUID.randomUUID();
        if (roomType == null) throw new IllegalArgumentException("RoomAssignment roomType must not be null");
        if (occupantIds == null || occupantIds.isEmpty()) {
            throw new IllegalArgumentException("RoomAssignment must have at least one occupant");
        }
        occupantIds = List.copyOf(occupantIds);
    }
}
