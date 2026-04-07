package com.gresk.modules.ticket.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class PaymentFailedException extends DomainException {

    public PaymentFailedException(String message) {
        super(message);
    }
}
