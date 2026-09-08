package com.gresk.modules.artist.domain.model.valueobject;

import java.util.UUID;

public record BandMemberId(UUID value) {

    public BandMemberId {
        if (value == null) throw new IllegalArgumentException("BandMemberId cannot be null");
    }

    public static BandMemberId generate() { return new BandMemberId(UUID.randomUUID()); }

    public static BandMemberId of(String value) {
        try {
            return new BandMemberId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid BandMemberId format: " + value, e);
        }
    }

    public static BandMemberId of(UUID value) { return new BandMemberId(value); }

    @Override
    public String toString() { return value.toString(); }
}
