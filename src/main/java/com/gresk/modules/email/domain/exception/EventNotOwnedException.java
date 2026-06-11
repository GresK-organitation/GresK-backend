package com.gresk.modules.email.domain.exception;

import java.util.UUID;

/** El evento no existe o no pertenece al promotor autenticado. */
public class EventNotOwnedException extends RuntimeException {
    public EventNotOwnedException(UUID eventId) {
        super("Event not found or not owned by promoter: " + eventId);
    }
}
