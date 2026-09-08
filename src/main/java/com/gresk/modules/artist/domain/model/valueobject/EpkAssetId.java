package com.gresk.modules.artist.domain.model.valueobject;

import java.util.UUID;

public record EpkAssetId(UUID value) {

    public EpkAssetId {
        if (value == null) throw new IllegalArgumentException("EpkAssetId cannot be null");
    }

    public static EpkAssetId generate() {
        return new EpkAssetId(UUID.randomUUID());
    }

    public static EpkAssetId of(String value) {
        try {
            return new EpkAssetId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EpkAssetId format: " + value, e);
        }
    }

    public static EpkAssetId of(UUID value) {
        return new EpkAssetId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
