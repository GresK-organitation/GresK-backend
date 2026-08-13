package com.gresk.modules.journal.domain.exception;

public class JournalEntryNotFoundException extends RuntimeException {
    public JournalEntryNotFoundException(String message) {
        super(message);
    }
}
