package com.gresk.modules.show.domain.exception;

import com.gresk.modules.show.domain.model.ShowStatus;

public class InvalidShowStatusTransitionException extends RuntimeException {
    public InvalidShowStatusTransitionException(ShowStatus from, ShowStatus to) {
        super("Cannot transition show from " + from + " to " + to);
    }
}
