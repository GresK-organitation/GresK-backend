package com.gresk.modules.agenda.domain.model;

import java.util.Objects;
import java.util.UUID;

public record CalendarSyncAccountId(UUID value) {

    public CalendarSyncAccountId {
        Objects.requireNonNull(value, "CalendarSyncAccountId value must not be null");
    }

    public static CalendarSyncAccountId generate() {
        return new CalendarSyncAccountId(UUID.randomUUID());
    }

    public static CalendarSyncAccountId of(String value) {
        try {
            return new CalendarSyncAccountId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CalendarSyncAccountId format: " + value, e);
        }
    }

    public static CalendarSyncAccountId of(UUID value) {
        return new CalendarSyncAccountId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
