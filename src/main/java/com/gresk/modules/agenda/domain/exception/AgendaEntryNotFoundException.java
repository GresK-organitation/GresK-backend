package com.gresk.modules.agenda.domain.exception;

public class AgendaEntryNotFoundException extends RuntimeException {
    public AgendaEntryNotFoundException(String id) {
        super("Agenda entry not found: " + id);
    }
}
