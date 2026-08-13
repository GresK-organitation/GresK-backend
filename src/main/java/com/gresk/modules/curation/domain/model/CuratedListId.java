package com.gresk.modules.curation.domain.model;

import java.util.UUID;

public record CuratedListId(UUID value) {

    public CuratedListId {
        if (value == null) throw new IllegalArgumentException("CuratedListId must not be null");
    }

    public static CuratedListId generate() { return new CuratedListId(UUID.randomUUID()); }

    public static CuratedListId of(UUID value) { return new CuratedListId(value); }

    public static CuratedListId of(String value) {
        try { return new CuratedListId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CuratedListId: " + value);
        }
    }

    @Override
    public String toString() { return value.toString(); }
}
