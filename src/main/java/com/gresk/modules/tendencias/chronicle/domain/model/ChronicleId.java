package com.gresk.modules.tendencias.chronicle.domain.model;

import java.util.UUID;

public record ChronicleId(UUID value) {

    public ChronicleId {
        if (value == null) throw new IllegalArgumentException("ChronicleId must not be null");
    }

    public static ChronicleId generate() { return new ChronicleId(UUID.randomUUID()); }

    public static ChronicleId of(UUID value) { return new ChronicleId(value); }

    public static ChronicleId of(String value) {
        try { return new ChronicleId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ChronicleId: " + value);
        }
    }

    @Override
    public String toString() { return value.toString(); }
}
