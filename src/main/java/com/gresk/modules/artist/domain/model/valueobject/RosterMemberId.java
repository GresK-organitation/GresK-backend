package com.gresk.modules.artist.domain.model.valueobject;

import java.util.UUID;

public record RosterMemberId(UUID value) {

    public RosterMemberId {
        if (value == null) throw new IllegalArgumentException("RosterMemberId cannot be null");
    }

    public static RosterMemberId generate() { return new RosterMemberId(UUID.randomUUID()); }

    public static RosterMemberId of(String value) {
        try {
            return new RosterMemberId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid RosterMemberId format: " + value, e);
        }
    }

    public static RosterMemberId of(UUID value) { return new RosterMemberId(value); }

    @Override
    public String toString() { return value.toString(); }
}
