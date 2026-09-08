package com.gresk.modules.finance.infrastructure.persistence.repository;

import com.gresk.modules.finance.infrastructure.persistence.entity.SupplierInvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupplierInvoiceJpaRepository extends JpaRepository<SupplierInvoiceEntity, UUID> {
    List<SupplierInvoiceEntity> findByLinkedEventId(UUID linkedEventId);
    List<SupplierInvoiceEntity> findByLinkedCostLineId(UUID linkedCostLineId);
}
