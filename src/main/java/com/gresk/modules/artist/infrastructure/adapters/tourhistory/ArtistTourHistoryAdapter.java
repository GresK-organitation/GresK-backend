package com.gresk.modules.artist.infrastructure.adapters.tourhistory;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistTourHistoryPort;
import com.gresk.modules.artist.domain.port.out.TourPerformanceRecord;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.model.TicketStatus;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Lookup de solo lectura a event/ticket/contract — sin persistencia propia,
 * calculado en caliente, mismo patrón que EventCatalogAdapter (discovery).
 *
 * grossRevenue: estimado (ticketsSold no cancelados × precio efectivo del evento) —
 * Ticket no persiste el precio realmente pagado en el momento de la compra.
 * feePaid/contractStatus: contrato SIGNED más reciente vinculado al evento; si no
 * hay ninguno SIGNED, el más reciente en cualquier estado; si no hay contrato,
 * feePaid=null y contractStatus="NO_CONTRACT".
 * estimatedMargin: grossRevenue - feePaid solo si las monedas coinciden.
 */
@Component
@RequiredArgsConstructor
public class ArtistTourHistoryAdapter implements ArtistTourHistoryPort {

    private final EventRepository        eventRepository;
    private final TicketRepository       ticketRepository;
    private final ContractRepositoryPort contractRepository;

    @Override
    public List<TourPerformanceRecord> findPastPerformances(ArtistId artistId) {
        EventFilter filter = new EventFilter(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(EventStatus.FINISHED), Optional.of(artistId.value())
        );
        PageRequest pageRequest = PageRequest.of(0, 500, Sort.by(Sort.Direction.DESC, "eventDate"));
        List<Event> pastEvents = eventRepository.findAll(filter, pageRequest);

        return pastEvents.stream()
                .map(this::toRecord)
                .sorted(Comparator.comparing(TourPerformanceRecord::eventDate).reversed())
                .toList();
    }

    private TourPerformanceRecord toRecord(Event event) {
        List<Ticket> tickets = ticketRepository.findByEventId(event.getId());
        long ticketsSold = tickets.stream().filter(t -> t.getStatus() != TicketStatus.CANCELLED).count();

        BigDecimal grossRevenue = null;
        String revenueCurrency = null;
        if (event.effectivePrice() != null) {
            grossRevenue = event.effectivePrice().amount().multiply(BigDecimal.valueOf(ticketsSold));
            revenueCurrency = event.effectivePrice().currency();
        }

        Contract contract = preferredContract(event.getId());
        BigDecimal feePaid = contract != null ? contract.getFeeAmount() : null;
        String feeCurrency = contract != null && contract.getFinancialTerms() != null
                ? contract.getFinancialTerms().feeCurrency() : null;
        String contractStatus = contract != null ? contract.getStatus().name() : "NO_CONTRACT";

        BigDecimal estimatedMargin = null;
        if (grossRevenue != null && feePaid != null
                && revenueCurrency != null && revenueCurrency.equalsIgnoreCase(feeCurrency)) {
            estimatedMargin = grossRevenue.subtract(feePaid);
        }

        String city  = event.getLocation() != null && event.getLocation().address() != null
                ? event.getLocation().address().city().value() : null;
        String venue = event.getLocation() != null ? event.getLocation().venue() : null;

        return new TourPerformanceRecord(
                event.getId().toString(),
                event.getTitle(),
                event.getEventDate().atZone(ZoneOffset.UTC).toLocalDate(),
                city,
                venue,
                event.getCapacity() != null ? event.getCapacity().total() : null,
                (int) ticketsSold,
                grossRevenue,
                revenueCurrency,
                feePaid,
                feeCurrency,
                estimatedMargin,
                contractStatus
        );
    }

    private Contract preferredContract(EventId eventId) {
        List<Contract> contracts = contractRepository.findByLinkedEventId(eventId.value());
        return contracts.stream()
                .filter(c -> c.getStatus() == ContractStatus.SIGNED)
                .findFirst()
                .or(() -> contracts.stream().max(Comparator.comparing(Contract::getUpdatedAt)))
                .orElse(null);
    }
}
