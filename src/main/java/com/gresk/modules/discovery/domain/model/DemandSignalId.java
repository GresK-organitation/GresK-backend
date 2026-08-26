package com.gresk.modules.discovery.domain.model;

import java.util.UUID;

public record DemandSignalId(UUID value) {

    public DemandSignalId {
        if (value == null) throw new IllegalArgumentException("DemandSignalId must not be null");
    }

    public static DemandSignalId generate() { return new DemandSignalId(UUID.randomUUID()); }

    public static DemandSignalId of(UUID value) { return new DemandSignalId(value); }

    @Override
    public String toString() { return value.toString(); }
}
