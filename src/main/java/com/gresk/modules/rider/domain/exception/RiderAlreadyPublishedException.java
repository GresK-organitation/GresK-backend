package com.gresk.modules.rider.domain.exception;

public class RiderAlreadyPublishedException extends RuntimeException {
    public RiderAlreadyPublishedException() {
        super("Rider is already published");
    }
}
