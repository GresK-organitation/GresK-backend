package com.gresk.modules.logistics.domain.model;

import java.util.UUID;

public record RoomingListId(UUID value) {

    public RoomingListId {
        if (value == null) throw new IllegalArgumentException("RoomingListId cannot be null");
    }

    public static RoomingListId generate() { return new RoomingListId(UUID.randomUUID()); }

    public static RoomingListId of(String value) {
        try {
            return new RoomingListId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid RoomingListId format: " + value, e);
        }
    }

    public static RoomingListId of(UUID value) { return new RoomingListId(value); }

    @Override
    public String toString() { return value.toString(); }
}
