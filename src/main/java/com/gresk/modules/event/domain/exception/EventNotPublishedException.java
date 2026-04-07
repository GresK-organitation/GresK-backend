package com.gresk.modules.event.domain.exception;

import com.gresk.shared.domain.exception.DomainException;

public class EventNotPublishedException extends DomainException {

    public EventNotPublishedException(String message) {
        super(message);
    }
}
