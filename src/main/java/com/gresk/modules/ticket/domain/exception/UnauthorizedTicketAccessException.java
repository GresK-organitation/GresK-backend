package com.gresk.modules.ticket.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class UnauthorizedTicketAccessException extends DomainException {

    public UnauthorizedTicketAccessException(String message) {
        super(message);
    }
}
