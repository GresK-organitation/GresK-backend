package com.gresk.modules.logistics.domain.model.valueobject;

import com.gresk.modules.logistics.domain.model.TravelRole;

import java.util.List;
import java.util.UUID;

/**
 * Miembro de la expedición dentro de un TravelParty. Vive como entrada de
 * TravelParty.members, mutado vía "reemplazo inmutable" (igual patrón que
 * booking.Milestone): cada transición devuelve una nueva instancia.
 */
public record TravelPartyMember(
        UUID id,
        PersonRef personRef,
        String displayName,
        TravelRole role,
        List<IndividualNeed> needs,
        RoomingPreference roomingPreference,
        boolean active
) {

    public TravelPartyMember {
        if (id == null) id = UUID.randomUUID();
        if (personRef == null) throw new IllegalArgumentException("TravelPartyMember personRef must not be null");
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("TravelPartyMember displayName must not be blank");
        }
        if (role == null) throw new IllegalArgumentException("TravelPartyMember role must not be null");
        needs = needs == null ? List.of() : List.copyOf(needs);
        roomingPreference = roomingPreference == null ? RoomingPreference.none() : roomingPreference;
    }

    public static TravelPartyMember of(PersonRef personRef, String displayName, TravelRole role,
                                        List<IndividualNeed> needs, RoomingPreference roomingPreference) {
        return new TravelPartyMember(UUID.randomUUID(), personRef, displayName, role, needs, roomingPreference, true);
    }

    public TravelPartyMember deactivate() {
        return new TravelPartyMember(id, personRef, displayName, role, needs, roomingPreference, false);
    }
}
