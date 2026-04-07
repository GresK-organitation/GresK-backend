package com.gresk.modules.ticket.domain.model;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.ticket.domain.exception.InvalidTicketStateException;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketTest {

    private final UserId userId = UserId.generate();
    private final EventId eventId = EventId.generate();
    private final QrCode qrCode = QrCode.of("test-qr-token");

    @Test
    void purchase_createsPurchasedTicketWithNonNullPurchasedAt() {
        Ticket ticket = Ticket.purchase(userId, eventId, qrCode);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.PURCHASED);
        assertThat(ticket.getPurchasedAt()).isNotNull();
        assertThat(ticket.getId()).isNotNull();
        assertThat(ticket.getUserId()).isEqualTo(userId);
        assertThat(ticket.getEventId()).isEqualTo(eventId);
        assertThat(ticket.getQrCode()).isEqualTo(qrCode);
    }

    @Test
    void markAsUsed_onPurchased_transitionsToUsed() {
        Ticket ticket = Ticket.purchase(userId, eventId, qrCode);

        ticket.markAsUsed();

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.USED);
    }

    @Test
    void markAsUsed_onUsed_throwsInvalidTicketStateException() {
        Ticket ticket = Ticket.purchase(userId, eventId, qrCode);
        ticket.markAsUsed();

        assertThatThrownBy(ticket::markAsUsed)
                .isInstanceOf(InvalidTicketStateException.class);
    }

    @Test
    void markAsUsed_onCancelled_throwsInvalidTicketStateException() {
        Ticket ticket = Ticket.purchase(userId, eventId, qrCode);
        ticket.cancel();

        assertThatThrownBy(ticket::markAsUsed)
                .isInstanceOf(InvalidTicketStateException.class);
    }

    @Test
    void cancel_onPurchased_transitionsToCancelled() {
        Ticket ticket = Ticket.purchase(userId, eventId, qrCode);

        ticket.cancel();

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CANCELLED);
    }

    @Test
    void cancel_onUsed_throwsInvalidTicketStateException() {
        Ticket ticket = Ticket.purchase(userId, eventId, qrCode);
        ticket.markAsUsed();

        assertThatThrownBy(ticket::cancel)
                .isInstanceOf(InvalidTicketStateException.class);
    }
}
