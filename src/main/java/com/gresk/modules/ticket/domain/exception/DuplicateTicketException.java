package com.gresk.modules.ticket.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class DuplicateTicketException extends DomainException {

    public DuplicateTicketException(String message) {
        super(message);
    }
}
