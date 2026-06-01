package com.gresk.modules.agenda.domain.exception;

public class InvalidAgendaEntryException extends RuntimeException {
    public InvalidAgendaEntryException(String message) {
        super(message);
    }
}
