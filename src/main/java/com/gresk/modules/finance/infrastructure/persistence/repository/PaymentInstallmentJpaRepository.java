package com.gresk.modules.finance.infrastructure.persistence.repository;

import com.gresk.modules.finance.infrastructure.persistence.entity.PaymentInstallmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentInstallmentJpaRepository extends JpaRepository<PaymentInstallmentEntity, UUID> {
    List<PaymentInstallmentEntity> findByLinkedContractId(UUID linkedContractId);
    List<PaymentInstallmentEntity> findByLinkedSettlementId(UUID linkedSettlementId);
    List<PaymentInstallmentEntity> findByPromoterIdAndStatus(UUID promoterId, String status);
}
