package com.gresk.modules.ticket.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class TicketAlreadyUsedException extends DomainException {

    public TicketAlreadyUsedException(String message) {
        super(message);
    }
}
