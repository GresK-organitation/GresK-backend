package com.gresk.modules.quotation.application.usecase;

import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.quotation.domain.exception.EventQuoteNotFoundException;
import com.gresk.modules.quotation.domain.model.EventQuote;
import com.gresk.modules.quotation.domain.model.QuoteId;
import com.gresk.modules.quotation.domain.port.out.EventQuoteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmEventQuoteUseCase {

    private final EventQuoteRepositoryPort eventQuoteRepository;

    @Transactional
    public EventQuote execute(String quoteId, String promoterId) {
        EventQuote quote = eventQuoteRepository.findById(QuoteId.of(quoteId))
                .orElseThrow(() -> new EventQuoteNotFoundException(quoteId));

        if (!quote.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ForbiddenOperationException("Quote does not belong to this promoter");
        }

        quote.confirm();
        return eventQuoteRepository.save(quote);
    }
}
