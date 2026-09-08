package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.query.GetEventInvoicesQuery;
import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.port.out.InvoiceRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEventInvoicesUseCase {

    private final InvoiceRepositoryPort invoiceRepository;

    public List<Invoice> execute(GetEventInvoicesQuery query) {
        PromoterId promoterId = PromoterId.of(query.promoterId());
        return invoiceRepository.findByLinkedEventId(UUID.fromString(query.linkedEventId())).stream()
                .filter(i -> i.getPromoterId().equals(promoterId))
                .toList();
    }
}
