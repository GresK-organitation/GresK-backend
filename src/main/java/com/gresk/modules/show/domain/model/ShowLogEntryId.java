package com.gresk.modules.show.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ShowLogEntryId(UUID value) {

    public ShowLogEntryId {
        Objects.requireNonNull(value, "ShowLogEntryId value must not be null");
    }

    public static ShowLogEntryId generate() {
        return new ShowLogEntryId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
