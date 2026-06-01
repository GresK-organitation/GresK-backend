package com.gresk.modules.rider.domain.exception;

public class RiderIncompletException extends RuntimeException {
    public RiderIncompletException(String field) {
        super("Cannot publish rider: " + field + " is missing or empty");
    }
}
