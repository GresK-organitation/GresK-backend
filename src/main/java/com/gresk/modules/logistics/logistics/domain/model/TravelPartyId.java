package com.gresk.modules.logistics.domain.model;

import java.util.UUID;

public record TravelPartyId(UUID value) {

    public TravelPartyId {
        if (value == null) throw new IllegalArgumentException("TravelPartyId cannot be null");
    }

    public static TravelPartyId generate() { return new TravelPartyId(UUID.randomUUID()); }

    public static TravelPartyId of(String value) {
        try {
            return new TravelPartyId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TravelPartyId format: " + value, e);
        }
    }

    public static TravelPartyId of(UUID value) { return new TravelPartyId(value); }

    @Override
    public String toString() { return value.toString(); }
}
