package com.gresk.modules.booking.domain.exception;

public class HoldExpirationRequiredException extends RuntimeException {
    public HoldExpirationRequiredException() {
        super("A hold (HOLD_1/HOLD_2) requires a future holdExpiresAt");
    }
}
