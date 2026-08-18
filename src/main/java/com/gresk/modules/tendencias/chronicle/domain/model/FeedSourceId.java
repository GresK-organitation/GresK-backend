package com.gresk.modules.tendencias.chronicle.domain.model;

import java.util.UUID;

public record FeedSourceId(UUID value) {

    public FeedSourceId {
        if (value == null) throw new IllegalArgumentException("FeedSourceId must not be null");
    }

    public static FeedSourceId generate() { return new FeedSourceId(UUID.randomUUID()); }

    public static FeedSourceId of(UUID value) { return new FeedSourceId(value); }

    public static FeedSourceId of(String value) {
        try { return new FeedSourceId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid FeedSourceId: " + value);
        }
    }

    @Override
    public String toString() { return value.toString(); }
}
