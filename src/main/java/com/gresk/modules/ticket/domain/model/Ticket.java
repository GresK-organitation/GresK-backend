package com.gresk.modules.ticket.domain.model;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.ticket.domain.exception.InvalidTicketStateException;
import com.gresk.modules.user.domain.model.UserId;

import java.time.ZonedDateTime;

public final class Ticket {

    private final TicketId id;
    private final UserId userId;
    private final EventId eventId;
    private final QrCode qrCode;
    private TicketStatus status;
    private final ZonedDateTime purchasedAt;

    private Ticket(TicketId id, UserId userId, EventId eventId,
                   QrCode qrCode, TicketStatus status, ZonedDateTime purchasedAt) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.qrCode = qrCode;
        this.status = status;
        this.purchasedAt = purchasedAt;
    }

    public static Ticket purchase(UserId userId, EventId eventId, QrCode qrCode) {
        return new Ticket(
                TicketId.generate(),
                userId,
                eventId,
                qrCode,
                TicketStatus.PURCHASED,
                ZonedDateTime.now()
        );
    }

    public static Ticket reconstitute(TicketId id, UserId userId, EventId eventId,
                                      QrCode qrCode, TicketStatus status, ZonedDateTime purchasedAt) {
        return new Ticket(id, userId, eventId, qrCode, status, purchasedAt);
    }

    public void markAsUsed() {
        if (status == TicketStatus.USED || status == TicketStatus.CANCELLED) {
            throw new InvalidTicketStateException(
                    "Cannot mark ticket as used: current status is " + status);
        }
        this.status = TicketStatus.USED;
    }

    public void cancel() {
        if (status == TicketStatus.USED) {
            throw new InvalidTicketStateException(
                    "Cannot cancel ticket: ticket has already been used");
        }
        if (status == TicketStatus.CANCELLED) {
            throw new InvalidTicketStateException(
                    "Cannot cancel ticket: ticket is already cancelled");
        }
        this.status = TicketStatus.CANCELLED;
    }

    public TicketId getId() { return id; }
    public UserId getUserId() { return userId; }
    public EventId getEventId() { return eventId; }
    public QrCode getQrCode() { return qrCode; }
    public TicketStatus getStatus() { return status; }
    public ZonedDateTime getPurchasedAt() { return purchasedAt; }
}
