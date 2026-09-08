package com.gresk.modules.booking.application.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class HoldConfirmedEvent extends ApplicationEvent {

    private final UUID bookingId;
    private final UUID artistId;

    public HoldConfirmedEvent(Object source, UUID bookingId, UUID artistId) {
        super(source);
        this.bookingId = bookingId;
        this.artistId = artistId;
    }

    public UUID getBookingId() { return bookingId; }
    public UUID getArtistId() { return artistId; }
}
