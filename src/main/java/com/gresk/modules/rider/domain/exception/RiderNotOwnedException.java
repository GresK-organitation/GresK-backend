package com.gresk.modules.rider.domain.exception;

public class RiderNotOwnedException extends RuntimeException {
    public RiderNotOwnedException(String riderId) {
        super("Rider " + riderId + " does not belong to the requesting promoter");
    }
}
