package com.gresk.modules.supplier.infrastructure.persistence.repository;

import com.gresk.modules.supplier.infrastructure.persistence.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SupplierJpaRepository extends JpaRepository<SupplierEntity, UUID> {

    List<SupplierEntity> findByPromoterId(UUID promoterId);

    @Query("""
            SELECT DISTINCT s FROM SupplierEntity s JOIN s.specialties spec
            WHERE s.promoterId = :promoterId AND spec = :category AND s.active = true
              AND (:city IS NULL OR LOWER(s.serviceCity) = LOWER(:city))
            """)
    List<SupplierEntity> findByPromoterAndCategory(
            @Param("promoterId") UUID promoterId,
            @Param("category") com.gresk.modules.supplier.domain.model.valueobject.SupplierCategory category,
            @Param("city") String city);
}
