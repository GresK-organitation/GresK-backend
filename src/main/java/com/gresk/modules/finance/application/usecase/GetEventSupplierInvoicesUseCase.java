package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.query.GetEventSupplierInvoicesQuery;
import com.gresk.modules.finance.domain.model.SupplierInvoice;
import com.gresk.modules.finance.domain.port.out.SupplierInvoiceRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEventSupplierInvoicesUseCase {

    private final SupplierInvoiceRepositoryPort supplierInvoiceRepository;

    public List<SupplierInvoice> execute(GetEventSupplierInvoicesQuery query) {
        PromoterId promoterId = PromoterId.of(query.promoterId());
        return supplierInvoiceRepository.findByLinkedEventId(UUID.fromString(query.linkedEventId())).stream()
                .filter(i -> i.getPromoterId().equals(promoterId))
                .toList();
    }
}
