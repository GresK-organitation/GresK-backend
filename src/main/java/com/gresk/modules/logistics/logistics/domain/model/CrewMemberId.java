package com.gresk.modules.logistics.domain.model;

import java.util.UUID;

public record CrewMemberId(UUID value) {

    public CrewMemberId {
        if (value == null) throw new IllegalArgumentException("CrewMemberId cannot be null");
    }

    public static CrewMemberId generate() { return new CrewMemberId(UUID.randomUUID()); }

    public static CrewMemberId of(String value) {
        try {
            return new CrewMemberId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CrewMemberId format: " + value, e);
        }
    }

    public static CrewMemberId of(UUID value) { return new CrewMemberId(value); }

    @Override
    public String toString() { return value.toString(); }
}
