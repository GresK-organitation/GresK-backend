package com.gresk.modules.agenda.domain.exception;

public class ForbiddenAgendaOperationException extends RuntimeException {
    public ForbiddenAgendaOperationException(String message) {
        super(message);
    }
}
