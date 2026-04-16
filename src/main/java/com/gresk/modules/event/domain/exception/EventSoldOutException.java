package com.gresk.modules.event.domain.exception;

public class EventSoldOutException extends RuntimeException {
    public EventSoldOutException(String eventId) {
        super("No hay entradas disponibles para el evento: " + eventId);
    }
}
