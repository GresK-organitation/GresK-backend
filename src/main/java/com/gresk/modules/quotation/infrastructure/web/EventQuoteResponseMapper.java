package com.gresk.modules.quotation.infrastructure.web;

import com.gresk.modules.quotation.domain.model.EventQuote;
import com.gresk.modules.quotation.domain.model.QuoteLine;
import org.springframework.stereotype.Component;

@Component
public class EventQuoteResponseMapper {

    public EventQuoteResponse toResponse(EventQuote quote) {
        return new EventQuoteResponse(
                quote.getId().toString(),
                quote.getEventId().toString(),
                quote.getPromoterId().value().toString(),
                quote.getCurrency(),
                quote.getStatus().name(),
                quote.getLines().stream().map(line -> toResponse(line, quote.getCurrency())).toList(),
                quote.totalEstimatedCost().amount(),
                quote.getCreatedAt(),
                quote.getUpdatedAt()
        );
    }

    private QuoteLineResponse toResponse(QuoteLine line, String currency) {
        return new QuoteLineResponse(
                line.getId(),
                line.getRiderItemRef().riderType().name(),
                line.getRiderItemRef().riderId(),
                line.getRiderItemRef().lineItemId(),
                line.getCategory().name(),
                line.getDescription(),
                line.getQuantity(),
                line.getFulfillmentSource().name(),
                line.getSupplierId().map(Object::toString).orElse(null),
                line.getCatalogItemId().orElse(null),
                line.getUnitCost().map(com.gresk.shared.domain.valueobject.Money::amount).orElse(null),
                line.getUnitCost().map(com.gresk.shared.domain.valueobject.Money::currency).orElse(null),
                line.subtotal(currency).amount()
        );
    }
}
