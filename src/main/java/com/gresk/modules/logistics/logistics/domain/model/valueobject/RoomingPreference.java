package com.gresk.modules.logistics.domain.model.valueobject;

import com.gresk.modules.logistics.domain.model.RoomType;

import java.util.List;
import java.util.UUID;

/**
 * Preferencias de habitación de un miembro del travel party, entrada del
 * RoomingListGenerator. preferredRoommateId solo empareja de forma automática
 * cuando es mutuo (A prefiere a B y B prefiere a A).
 */
public record RoomingPreference(RoomType preferredRoomType, UUID preferredRoommateId, List<UUID> doNotShareWith) {

    public RoomingPreference {
        doNotShareWith = doNotShareWith == null ? List.of() : List.copyOf(doNotShareWith);
    }

    public static RoomingPreference none() {
        return new RoomingPreference(null, null, List.of());
    }
}
