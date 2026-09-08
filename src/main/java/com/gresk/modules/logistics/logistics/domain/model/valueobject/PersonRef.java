package com.gresk.modules.logistics.domain.model.valueobject;

import com.gresk.modules.logistics.domain.model.PersonType;

import java.util.UUID;

/**
 * Identifica a la persona real detrás de un TravelPartyMember: o bien un
 * artist.BandMemberId (músico) o un logistics.CrewMemberId (crew propio).
 */
public record PersonRef(PersonType type, UUID personId) {

    public PersonRef {
        if (type == null) throw new IllegalArgumentException("PersonRef type must not be null");
        if (personId == null) throw new IllegalArgumentException("PersonRef personId must not be null");
    }

    public static PersonRef bandMember(UUID id) { return new PersonRef(PersonType.BAND_MEMBER, id); }

    public static PersonRef crew(UUID id) { return new PersonRef(PersonType.CREW, id); }
}
