package com.gresk.modules.quotation.domain.port.out;

import com.gresk.modules.quotation.domain.model.EventQuote;
import com.gresk.modules.quotation.domain.model.QuoteId;

import java.util.Optional;
import java.util.UUID;

public interface EventQuoteRepositoryPort {

    EventQuote save(EventQuote quote);

    Optional<EventQuote> findById(QuoteId id);

    Optional<EventQuote> findByEventId(UUID eventId);
}
