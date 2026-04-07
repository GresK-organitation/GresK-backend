package com.gresk.modules.ticket.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class TicketNotFoundException extends DomainException {

    public TicketNotFoundException(String message) {
        super(message);
    }
}
