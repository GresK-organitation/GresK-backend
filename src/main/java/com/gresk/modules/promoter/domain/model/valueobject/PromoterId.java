package com.gresk.modules.promoter.domain.model.valueobject;

import java.util.UUID;

public record PromoterId (UUID value){
    public PromoterId {
        if (value == null) throw new IllegalArgumentException("PromoterId cannot be null");
    }

    public static PromoterId generate() {
        return new PromoterId(UUID.randomUUID());
    }

    public static PromoterId of(String value) {
        try {
            return new PromoterId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid PromoterId format: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
