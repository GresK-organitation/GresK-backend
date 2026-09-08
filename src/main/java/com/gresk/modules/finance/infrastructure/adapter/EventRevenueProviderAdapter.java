package com.gresk.modules.finance.infrastructure.adapter;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.finance.domain.exception.EventNotFoundException;
import com.gresk.modules.finance.domain.model.valueobject.EventRevenueSnapshot;
import com.gresk.modules.finance.domain.port.out.EventRevenueProviderPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.model.TicketStatus;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Lookup de solo lectura hacia event/ticket, sin persistencia propia — mismo patrón que
 * ArtistTourHistoryAdapter (artist.infrastructure.adapters.tourhistory): calcula la
 * taquilla bruta en caliente (tickets no cancelados x precio efectivo del evento).
 */
@Component
@RequiredArgsConstructor
public class EventRevenueProviderAdapter implements EventRevenueProviderPort {

    private final EventRepository  eventRepository;
    private final TicketRepository ticketRepository;

    @Override
    public EventRevenueSnapshot getRevenueSnapshot(UUID eventId) {
        Event event = findEvent(eventId);
        List<Ticket> tickets = ticketRepository.findByEventId(event.getId());
        long ticketsSold = tickets.stream().filter(t -> t.getStatus() != TicketStatus.CANCELLED).count();

        Money grossBoxOffice;
        if (event.effectivePrice() != null) {
            grossBoxOffice = new Money(event.effectivePrice().amount(), event.effectivePrice().currency())
                    .multiply((int) ticketsSold);
        } else {
            grossBoxOffice = Money.zero("EUR");
        }

        return new EventRevenueSnapshot(grossBoxOffice, (int) ticketsSold, Instant.now());
    }

    @Override
    public PromoterId getPromoterIdForEvent(UUID eventId) {
        return findEvent(eventId).getPromoterId();
    }

    private Event findEvent(UUID eventId) {
        return eventRepository.findById(new EventId(eventId))
                .orElseThrow(() -> new EventNotFoundException(eventId.toString()));
    }
}
