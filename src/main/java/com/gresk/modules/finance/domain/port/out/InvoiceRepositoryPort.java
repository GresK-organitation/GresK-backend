package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.finance.domain.model.Invoice;
import com.gresk.modules.finance.domain.model.InvoiceId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepositoryPort {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(InvoiceId id);
    List<Invoice> findByLinkedEventId(UUID linkedEventId);
    List<Invoice> findByPromoterId(PromoterId promoterId);
    int countByPromoterIdForYear(PromoterId promoterId, int year);
}
