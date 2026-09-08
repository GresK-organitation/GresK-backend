package com.gresk.modules.quotation.infrastructure.persistence.adapter;

import com.gresk.modules.quotation.domain.model.EventQuote;
import com.gresk.modules.quotation.domain.model.QuoteId;
import com.gresk.modules.quotation.domain.port.out.EventQuoteRepositoryPort;
import com.gresk.modules.quotation.infrastructure.persistence.mapper.EventQuoteMapper;
import com.gresk.modules.quotation.infrastructure.persistence.repository.EventQuoteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEventQuoteRepositoryAdapter implements EventQuoteRepositoryPort {

    private final EventQuoteJpaRepository repo;
    private final EventQuoteMapper        mapper;

    @Override
    @Transactional
    public EventQuote save(EventQuote quote) {
        return mapper.toDomain(repo.save(mapper.toEntity(quote)));
    }

    @Override
    public Optional<EventQuote> findById(QuoteId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<EventQuote> findByEventId(UUID eventId) {
        return repo.findByEventId(eventId).map(mapper::toDomain);
    }
}
