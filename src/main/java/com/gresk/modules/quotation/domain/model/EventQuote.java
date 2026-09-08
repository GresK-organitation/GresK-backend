package com.gresk.modules.quotation.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.quotation.domain.exception.QuoteLineNotFoundException;
import com.gresk.shared.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EventQuote {

    private final QuoteId id;
    private final UUID eventId;
    private final PromoterId promoterId;
    private final String currency;
    private final Instant createdAt;

    private QuoteStatus status;
    private final List<QuoteLine> lines;
    private Instant updatedAt;

    private EventQuote(QuoteId id, UUID eventId, PromoterId promoterId, String currency, QuoteStatus status,
                        List<QuoteLine> lines, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.eventId = eventId;
        this.promoterId = promoterId;
        this.currency = currency;
        this.status = status;
        this.lines = lines != null ? new ArrayList<>(lines) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EventQuote open(UUID eventId, PromoterId promoterId, String currency) {
        Instant now = Instant.now();
        return new EventQuote(QuoteId.generate(), eventId, promoterId, currency, QuoteStatus.DRAFT,
                List.of(), now, now);
    }

    public static EventQuote reconstitute(QuoteId id, UUID eventId, PromoterId promoterId, String currency,
                                           QuoteStatus status, List<QuoteLine> lines,
                                           Instant createdAt, Instant updatedAt) {
        return new EventQuote(id, eventId, promoterId, currency, status, lines, createdAt, updatedAt);
    }

    public void addLine(QuoteLine line) {
        requireEditable();
        lines.add(line);
        touch();
    }

    public QuoteLine line(UUID lineId) {
        return lines.stream().filter(l -> l.getId().equals(lineId)).findFirst()
                .orElseThrow(() -> new QuoteLineNotFoundException(lineId));
    }

    public void confirm() {
        requireEditable();
        this.status = QuoteStatus.CONFIRMED;
        touch();
    }

    public Money totalEstimatedCost() {
        Money total = Money.zero(currency);
        for (QuoteLine line : lines) total = total.add(line.subtotal(currency));
        return total;
    }

    private void requireEditable() {
        if (status == QuoteStatus.CONFIRMED) throw new IllegalStateException("Cannot modify a confirmed quote");
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public QuoteId getId() { return id; }
    public UUID getEventId() { return eventId; }
    public PromoterId getPromoterId() { return promoterId; }
    public String getCurrency() { return currency; }
    public QuoteStatus getStatus() { return status; }
    public List<QuoteLine> getLines() { return List.copyOf(lines); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
