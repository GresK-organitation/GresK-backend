package com.gresk.modules.quotation.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.quotation.application.command.GenerateEventQuoteCommand;
import com.gresk.modules.quotation.domain.exception.EventHasNoArtistException;
import com.gresk.modules.quotation.domain.exception.EventQuoteAlreadyExistsException;
import com.gresk.modules.quotation.domain.model.EventQuote;
import com.gresk.modules.quotation.domain.model.QuoteLine;
import com.gresk.modules.quotation.domain.model.valueobject.RiderItemReference;
import com.gresk.modules.quotation.domain.model.valueobject.RiderType;
import com.gresk.modules.quotation.domain.port.out.EventQuoteRepositoryPort;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderItemCategory;
import com.gresk.modules.rider.domain.model.RiderLineItem;
import com.gresk.modules.rider.domain.model.RiderStatus;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import com.gresk.modules.rider.domain.port.out.RiderRepositoryPort;
import com.gresk.modules.supplier.domain.model.CatalogItem;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory;
import com.gresk.modules.supplier.domain.port.out.SupplierRepositoryPort;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenerateEventQuoteUseCase {

    private final EventQuoteRepositoryPort eventQuoteRepository;
    private final EventRepository eventRepository;
    private final RiderRepositoryPort technicalRiderRepository;
    private final HospitalityRiderRepositoryPort hospitalityRiderRepository;
    private final SupplierRepositoryPort supplierRepository;

    @Transactional
    public EventQuote execute(GenerateEventQuoteCommand command) {
        EventId eventId = EventId.of(command.eventId());
        PromoterId promoterId = PromoterId.of(command.promoterId());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + command.eventId()));
        if (!event.getPromoterId().equals(promoterId)) {
            throw new ForbiddenOperationException("Event does not belong to this promoter");
        }
        if (event.getArtistId() == null) {
            throw new EventHasNoArtistException(command.eventId());
        }
        if (eventQuoteRepository.findByEventId(eventId.value()).isPresent()) {
            throw new EventQuoteAlreadyExistsException(command.eventId());
        }

        ArtistId artistId = ArtistId.of(event.getArtistId());
        EventQuote quote = EventQuote.open(eventId.value(), promoterId, command.currency());

        latestPublished(technicalRiderRepository.findByArtistId(artistId), TechnicalRider::getStatus,
                TechnicalRider::getUpdatedAt)
                .ifPresent(rider -> rider.getLineItems().forEach(item ->
                        quote.addLine(buildQuoteLine(RiderType.TECHNICAL, rider.getId().value(), item, promoterId))));

        latestPublished(hospitalityRiderRepository.findByArtistId(artistId), HospitalityRider::getStatus,
                HospitalityRider::getUpdatedAt)
                .ifPresent(rider -> rider.getLineItems().forEach(item ->
                        quote.addLine(buildQuoteLine(RiderType.HOSPITALITY, rider.getId().value(), item, promoterId))));

        return eventQuoteRepository.save(quote);
    }

    private <T> Optional<T> latestPublished(List<T> riders, java.util.function.Function<T, RiderStatus> statusFn,
                                             java.util.function.Function<T, java.time.Instant> updatedAtFn) {
        return riders.stream()
                .filter(r -> statusFn.apply(r) == RiderStatus.PUBLISHED)
                .max(Comparator.comparing(updatedAtFn));
    }

    private QuoteLine buildQuoteLine(RiderType riderType, java.util.UUID riderId, RiderLineItem item, PromoterId promoterId) {
        RiderItemReference ref = new RiderItemReference(riderType, riderId, item.getId());
        QuoteLine line = QuoteLine.fromRiderItem(ref, item.getCategory(), item.getDescription(),
                item.getQuantity(), item.getFulfillmentSource());

        findCheapestCatalogMatch(promoterId, item.getCategory())
                .ifPresent(match -> line.assignSupplier(match.supplier().getId(), match.catalogItem().getId(),
                        match.catalogItem().getUnitPrice()));

        return line;
    }

    private Optional<SupplierCatalogMatch> findCheapestCatalogMatch(PromoterId promoterId, RiderItemCategory category) {
        SupplierCategory supplierCategory = toSupplierCategory(category);
        List<Supplier> candidates = supplierRepository.findByPromoterAndCategory(promoterId, supplierCategory, null);

        Optional<SupplierCatalogMatch> best = Optional.empty();
        for (Supplier supplier : candidates) {
            for (CatalogItem catalogItem : supplier.getCatalog()) {
                if (catalogItem.getCategory() != supplierCategory || !catalogItem.isActive()) continue;
                if (best.isEmpty() || isCheaper(catalogItem.getUnitPrice(), best.get().catalogItem().getUnitPrice())) {
                    best = Optional.of(new SupplierCatalogMatch(supplier, catalogItem));
                }
            }
        }
        return best;
    }

    private boolean isCheaper(Money candidate, Money current) {
        return candidate.currency().equals(current.currency()) && candidate.amount().compareTo(current.amount()) < 0;
    }

    private SupplierCategory toSupplierCategory(RiderItemCategory category) {
        return switch (category) {
            case SOUND_PA, MICROPHONE -> SupplierCategory.SOUND;
            case BACKLINE -> SupplierCategory.BACKLINE_RENTAL;
            case LIGHTING -> SupplierCategory.LIGHTING;
            case STAGE -> SupplierCategory.STAGING;
            case CATERING, DIET -> SupplierCategory.CATERING;
            case ACCOMMODATION -> SupplierCategory.ACCOMMODATION;
            case TRANSPORT -> SupplierCategory.TRANSPORT;
            case DRESSING_ROOM, OTHER -> SupplierCategory.OTHER;
        };
    }

    private record SupplierCatalogMatch(Supplier supplier, CatalogItem catalogItem) {}
}
