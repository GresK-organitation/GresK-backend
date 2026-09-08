package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.finance.domain.model.SupplierInvoice;
import com.gresk.modules.finance.domain.model.SupplierInvoiceId;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierInvoiceRepositoryPort {
    SupplierInvoice save(SupplierInvoice invoice);
    Optional<SupplierInvoice> findById(SupplierInvoiceId id);
    List<SupplierInvoice> findByLinkedEventId(UUID linkedEventId);
    List<SupplierInvoice> findByLinkedCostLineId(CostLineId costLineId);
}
