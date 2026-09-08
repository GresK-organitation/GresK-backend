package com.gresk.modules.artist.domain.model.valueobject;

import java.util.UUID;

public record EpkShareLinkId(UUID value) {

    public EpkShareLinkId {
        if (value == null) throw new IllegalArgumentException("EpkShareLinkId cannot be null");
    }

    public static EpkShareLinkId generate() { return new EpkShareLinkId(UUID.randomUUID()); }

    public static EpkShareLinkId of(String value) {
        try {
            return new EpkShareLinkId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EpkShareLinkId format: " + value, e);
        }
    }

    public static EpkShareLinkId of(UUID value) { return new EpkShareLinkId(value); }

    @Override
    public String toString() { return value.toString(); }
}
