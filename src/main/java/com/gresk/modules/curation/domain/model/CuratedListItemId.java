package com.gresk.modules.curation.domain.model;

import java.util.UUID;

public record CuratedListItemId(UUID value) {

    public CuratedListItemId {
        if (value == null) throw new IllegalArgumentException("CuratedListItemId must not be null");
    }

    public static CuratedListItemId generate() { return new CuratedListItemId(UUID.randomUUID()); }

    public static CuratedListItemId of(UUID value) { return new CuratedListItemId(value); }

    public static CuratedListItemId of(String value) {
        try { return new CuratedListItemId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CuratedListItemId: " + value);
        }
    }

    @Override
    public String toString() { return value.toString(); }
}
