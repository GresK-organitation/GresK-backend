package com.gresk.modules.booking.application.event;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

public class BookingRescheduledEvent extends ApplicationEvent {

    private final UUID bookingId;
    private final Instant newEventDate;

    public BookingRescheduledEvent(Object source, UUID bookingId, Instant newEventDate) {
        super(source);
        this.bookingId = bookingId;
        this.newEventDate = newEventDate;
    }

    public UUID getBookingId() { return bookingId; }
    public Instant getNewEventDate() { return newEventDate; }
}
