package com.gresk.modules.journal.domain.model;

import java.util.UUID;

public record JournalMediaId(UUID value) {

    public JournalMediaId {
        if (value == null) throw new IllegalArgumentException("JournalMediaId must not be null");
    }

    public static JournalMediaId generate() { return new JournalMediaId(UUID.randomUUID()); }

    public static JournalMediaId of(UUID value) { return new JournalMediaId(value); }

    public static JournalMediaId of(String value) {
        try { return new JournalMediaId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid JournalMediaId: " + value);
        }
    }

    @Override
    public String toString() { return value.toString(); }
}
