package com.gresk.modules.rider.domain.model;

import java.util.UUID;

public record ChecklistId(UUID value) {

    public ChecklistId {
        if (value == null) throw new IllegalArgumentException("ChecklistId cannot be null");
    }

    public static ChecklistId generate() {
        return new ChecklistId(UUID.randomUUID());
    }

    public static ChecklistId of(String value) {
        try {
            return new ChecklistId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ChecklistId format: " + value, e);
        }
    }

    public static ChecklistId of(UUID value) {
        return new ChecklistId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
