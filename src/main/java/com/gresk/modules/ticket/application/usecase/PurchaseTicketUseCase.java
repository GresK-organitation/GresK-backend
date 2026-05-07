package com.gresk.modules.ticket.application.usecase;

import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.ticket.application.port.in.PurchaseTicketPort;
import com.gresk.modules.ticket.domain.exception.DuplicateTicketException;
import com.gresk.modules.ticket.domain.exception.PaymentFailedException;
import com.gresk.modules.ticket.domain.model.PaymentResult;
import com.gresk.modules.ticket.domain.model.QrCode;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.ticket.domain.port.out.PaymentGateway;
import com.gresk.modules.ticket.domain.port.out.QrCodeGenerator;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseTicketUseCase implements PurchaseTicketPort {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final PaymentGateway paymentGateway;
    private final QrCodeGenerator qrCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Ticket execute(PurchaseTicketCommand command) {
        UserId userId = UserId.from(command.userId());
        EventId eventId = EventId.of(command.eventId());

        if (ticketRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new DuplicateTicketException(
                    "User already has a ticket for this event");
        }

        Event event = (Event) eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with ID: " + eventId.value()
                ));

        event.decrementCapacity();

        PaymentResult paymentResult = paymentGateway.processPayment(userId, eventId, event.getPrice());
        if (!paymentResult.success()) {
            throw new PaymentFailedException("Payment failed for transaction: " + paymentResult.transactionId());
        }

        TicketId ticketId = TicketId.generate();
        QrCode qrCode = qrCodeGenerator.generate(ticketId);
        Ticket ticket = Ticket.purchase(userId, eventId, qrCode);

        ticketRepository.save(ticket);
        eventRepository.save(event);

        return ticket;
    }
}
