package com.gresk.modules.journal.domain.exception;

public class InvalidJournalEntryException extends RuntimeException {
    public InvalidJournalEntryException(String message) {
        super(message);
    }
}
