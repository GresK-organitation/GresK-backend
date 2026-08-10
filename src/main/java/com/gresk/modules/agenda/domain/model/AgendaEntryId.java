package com.gresk.modules.agenda.domain.model;

import java.util.Objects;
import java.util.UUID;

public record AgendaEntryId(UUID value) {

    public AgendaEntryId {
        Objects.requireNonNull(value, "AgendaEntryId value must not be null");
    }

    public static AgendaEntryId generate() {
        return new AgendaEntryId(UUID.randomUUID());
    }

    public static AgendaEntryId of(String value) {
        try {
            return new AgendaEntryId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AgendaEntryId format: " + value, e);
        }
    }

    public static AgendaEntryId of(UUID value) {
        return new AgendaEntryId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
