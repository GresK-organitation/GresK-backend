package com.gresk.modules.finance.domain.model;

import java.util.UUID;

public record SettlementId(UUID value) {

    public SettlementId {
        if (value == null) throw new IllegalArgumentException("SettlementId cannot be null");
    }

    public static SettlementId generate() {
        return new SettlementId(UUID.randomUUID());
    }

    public static SettlementId of(String value) {
        try {
            return new SettlementId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid SettlementId format: " + value, e);
        }
    }

    public static SettlementId of(UUID value) {
        return new SettlementId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
