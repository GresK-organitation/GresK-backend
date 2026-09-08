package com.gresk.modules.finance.domain.model.valueobject;

import java.util.UUID;

public record CostLineId(UUID value) {

    public CostLineId {
        if (value == null) throw new IllegalArgumentException("CostLineId cannot be null");
    }

    public static CostLineId generate() {
        return new CostLineId(UUID.randomUUID());
    }

    public static CostLineId of(String value) {
        try {
            return new CostLineId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CostLineId format: " + value, e);
        }
    }

    public static CostLineId of(UUID value) {
        return new CostLineId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
