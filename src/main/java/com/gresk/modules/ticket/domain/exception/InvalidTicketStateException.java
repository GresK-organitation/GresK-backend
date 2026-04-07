package com.gresk.modules.ticket.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class InvalidTicketStateException extends DomainException {

    public InvalidTicketStateException(String message) {
        super(message);
    }
}
