package com.gresk.modules.booking.domain.exception;

import com.gresk.modules.booking.domain.model.BookingStatus;

public class InvalidBookingStatusTransitionException extends RuntimeException {
    public InvalidBookingStatusTransitionException(BookingStatus from, BookingStatus to) {
        super("Cannot transition booking from " + from + " to " + to);
    }
}
