package com.gresk.shared.domain.valueobject;

public record AssetId(String value) {

    public AssetId {
        if (value == null || value.isBlank()) {
            value = "";
        } else {
            value = value.trim();
        }
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public static AssetId of(String value) {
        return new AssetId(value);
    }

    public static AssetId reconstitute(String value) {
        return new AssetId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

