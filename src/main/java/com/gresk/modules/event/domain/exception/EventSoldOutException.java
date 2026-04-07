package com.gresk.modules.event.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class EventSoldOutException extends DomainException {

    public EventSoldOutException(String message) {
        super(message);
    }
}
