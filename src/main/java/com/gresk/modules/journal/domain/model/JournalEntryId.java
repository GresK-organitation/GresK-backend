package com.gresk.modules.journal.domain.model;

import java.util.UUID;

public record JournalEntryId(UUID value) {

    public JournalEntryId {
        if (value == null) throw new IllegalArgumentException("JournalEntryId must not be null");
    }

    public static JournalEntryId generate() { return new JournalEntryId(UUID.randomUUID()); }

    public static JournalEntryId of(UUID value) { return new JournalEntryId(value); }

    public static JournalEntryId of(String value) {
        try { return new JournalEntryId(UUID.fromString(value)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid JournalEntryId: " + value);
        }
    }

    @Override
    public String toString() { return value.toString(); }
}
