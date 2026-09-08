package com.gresk.modules.finance.infrastructure.persistence.repository;

import com.gresk.modules.finance.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID> {
    List<InvoiceEntity> findByLinkedEventId(UUID linkedEventId);
    List<InvoiceEntity> findByPromoterId(UUID promoterId);

    @Query("SELECT COUNT(i) FROM InvoiceEntity i WHERE i.promoterId = :promoterId AND i.invoiceNumber LIKE :yearPrefix%")
    int countByPromoterIdAndYearPrefix(@Param("promoterId") UUID promoterId, @Param("yearPrefix") String yearPrefix);
}
