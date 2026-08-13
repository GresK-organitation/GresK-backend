package com.gresk.modules.journal.domain.exception;

public class JournalEntryForbiddenException extends RuntimeException {
    public JournalEntryForbiddenException(String message) {
        super(message);
    }
}
