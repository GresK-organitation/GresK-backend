package com.gresk.modules.quotation.application.usecase;

import com.gresk.modules.quotation.domain.exception.EventQuoteNotFoundException;
import com.gresk.modules.quotation.domain.model.EventQuote;
import com.gresk.modules.quotation.domain.port.out.EventQuoteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetEventQuoteUseCase {

    private final EventQuoteRepositoryPort eventQuoteRepository;

    @Transactional(readOnly = true)
    public EventQuote execute(String eventId) {
        return eventQuoteRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventQuoteNotFoundException(eventId));
    }
}
