package com.gresk.modules.quotation.application.usecase;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.quotation.domain.exception.EventQuoteNotFoundException;
import com.gresk.modules.quotation.domain.model.EventQuote;
import com.gresk.modules.quotation.domain.model.QuoteId;
import com.gresk.modules.quotation.domain.model.QuoteLine;
import com.gresk.modules.quotation.domain.port.out.EventQuoteRepositoryPort;
import com.gresk.modules.supplier.domain.exception.SupplierNotFoundException;
import com.gresk.modules.supplier.domain.model.CatalogItem;
import com.gresk.modules.supplier.domain.model.Supplier;
import com.gresk.modules.supplier.domain.model.SupplierId;
import com.gresk.modules.supplier.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignSupplierToQuoteLineUseCase {

    private final EventQuoteRepositoryPort eventQuoteRepository;
    private final SupplierRepositoryPort supplierRepository;

    @Transactional
    public QuoteLine execute(String quoteId, String promoterId, String quoteLineId, String supplierId, String catalogItemId) {
        EventQuote quote = eventQuoteRepository.findById(QuoteId.of(quoteId))
                .orElseThrow(() -> new EventQuoteNotFoundException(quoteId));

        if (!quote.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new com.gresk.modules.event.domain.exception.ForbiddenOperationException(
                    "Quote does not belong to this promoter");
        }

        Supplier supplier = supplierRepository.findById(SupplierId.of(supplierId))
                .orElseThrow(() -> new SupplierNotFoundException(supplierId));
        CatalogItem catalogItem = supplier.catalogItem(UUID.fromString(catalogItemId));

        QuoteLine line = quote.line(UUID.fromString(quoteLineId));
        line.assignSupplier(supplier.getId(), catalogItem.getId(), catalogItem.getUnitPrice());

        eventQuoteRepository.save(quote);
        return line;
    }
}
