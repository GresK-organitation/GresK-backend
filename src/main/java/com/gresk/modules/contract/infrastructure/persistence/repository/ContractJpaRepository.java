package com.gresk.modules.contract.infrastructure.persistence.repository;

import com.gresk.modules.contract.infrastructure.persistence.entity.ContractEntity;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractJpaRepository extends JpaRepository<ContractEntity, UUID> {

    List<ContractEntity> findByPromoterId(UUID promoterId);

    List<ContractEntity> findByPromoterIdAndStatus(UUID promoterId, ContractStatus status);

    List<ContractEntity> findByPromoterIdAndType(UUID promoterId, ContractType type);

    Optional<ContractEntity> findByShareToken(String shareToken);

    List<ContractEntity> findByLinkedEventId(UUID linkedEventId);

    @Query("SELECT COUNT(c) FROM ContractEntity c WHERE c.promoterId = :promoterId AND c.referenceNumber LIKE :prefix%")
    int countByPromoterIdAndYearPrefix(@Param("promoterId") UUID promoterId, @Param("prefix") String prefix);

    @Query("SELECT c.status, COUNT(c) FROM ContractEntity c WHERE c.promoterId = :promoterId GROUP BY c.status")
    List<Object[]> countGroupedByStatus(@Param("promoterId") UUID promoterId);

    @Query("SELECT c.type, COUNT(c) FROM ContractEntity c WHERE c.promoterId = :promoterId GROUP BY c.type")
    List<Object[]> countGroupedByType(@Param("promoterId") UUID promoterId);

    @Query("SELECT COALESCE(SUM(c.feeAmount), 0) FROM ContractEntity c WHERE c.promoterId = :promoterId AND c.status = 'SIGNED'")
    BigDecimal sumSignedFeeByPromoter(@Param("promoterId") UUID promoterId);
}
