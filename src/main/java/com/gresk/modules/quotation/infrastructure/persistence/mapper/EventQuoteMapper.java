package com.gresk.modules.quotation.infrastructure.persistence.mapper;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.quotation.domain.model.EventQuote;
import com.gresk.modules.quotation.domain.model.QuoteId;
import com.gresk.modules.quotation.domain.model.QuoteLine;
import com.gresk.modules.quotation.domain.model.valueobject.RiderItemReference;
import com.gresk.modules.quotation.infrastructure.persistence.entity.EventQuoteEntity;
import com.gresk.modules.quotation.infrastructure.persistence.entity.QuoteLineEntity;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventQuoteMapper {

    public EventQuote toDomain(EventQuoteEntity e) {
        List<QuoteLine> lines = e.getLines().stream().map(this::toDomainLine).toList();

        return EventQuote.reconstitute(
                QuoteId.of(e.getId()), e.getEventId(), PromoterId.of(e.getPromoterId()), e.getCurrency(),
                e.getStatus(), lines, e.getCreatedAt(), e.getUpdatedAt());
    }

    public EventQuoteEntity toEntity(EventQuote q) {
        EventQuoteEntity entity = EventQuoteEntity.builder()
                .id(q.getId().value())
                .eventId(q.getEventId())
                .promoterId(q.getPromoterId().value())
                .currency(q.getCurrency())
                .status(q.getStatus())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .build();

        List<QuoteLineEntity> lines = new ArrayList<>();
        for (QuoteLine line : q.getLines()) {
            lines.add(toEntityLine(line, entity));
        }
        entity.setLines(lines);

        return entity;
    }

    private QuoteLine toDomainLine(QuoteLineEntity e) {
        RiderItemReference ref = new RiderItemReference(e.getRiderType(), e.getRiderId(), e.getLineItemId());
        Money unitCost = e.getUnitCostAmount() == null ? null : Money.of(e.getUnitCostAmount(), e.getUnitCostCurrency());
        SupplierId supplierId = e.getSupplierId() == null ? null : SupplierId.of(e.getSupplierId());

        return QuoteLine.reconstitute(e.getId(), ref, e.getCategory(), e.getDescription(), e.getQuantity(),
                e.getFulfillmentSource(), supplierId, e.getCatalogItemId(), unitCost);
    }

    private QuoteLineEntity toEntityLine(QuoteLine line, EventQuoteEntity parent) {
        QuoteLineEntity.QuoteLineEntityBuilder builder = QuoteLineEntity.builder()
                .id(line.getId())
                .quote(parent)
                .riderType(line.getRiderItemRef().riderType())
                .riderId(line.getRiderItemRef().riderId())
                .lineItemId(line.getRiderItemRef().lineItemId())
                .category(line.getCategory())
                .description(line.getDescription())
                .quantity(line.getQuantity())
                .fulfillmentSource(line.getFulfillmentSource());

        line.getSupplierId().ifPresent(id -> builder.supplierId(id.value()));
        line.getCatalogItemId().ifPresent(builder::catalogItemId);
        line.getUnitCost().ifPresent(cost -> builder.unitCostAmount(cost.amount()).unitCostCurrency(cost.currency()));

        return builder.build();
    }
}
