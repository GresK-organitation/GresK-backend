package com.gresk.modules.ticket.domain.model;

import java.util.UUID;

public record TicketId(UUID value) {

    public TicketId {
        if (value == null) {
            throw new IllegalArgumentException("TicketId value must not be null");
        }
    }

    public static TicketId generate() {
        return new TicketId(UUID.randomUUID());
    }

    public static TicketId from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TicketId string must not be null or blank");
        }
        try {
            return new TicketId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TicketId format: " + value);
        }
    }
}
