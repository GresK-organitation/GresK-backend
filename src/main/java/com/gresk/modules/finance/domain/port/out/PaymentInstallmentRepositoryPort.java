package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.finance.domain.model.InstallmentStatus;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.model.PaymentInstallmentId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentInstallmentRepositoryPort {
    PaymentInstallment save(PaymentInstallment installment);
    Optional<PaymentInstallment> findById(PaymentInstallmentId id);
    List<PaymentInstallment> findByLinkedContractId(UUID linkedContractId);
    List<PaymentInstallment> findByLinkedSettlementId(UUID linkedSettlementId);
    List<PaymentInstallment> findByPromoterIdAndStatus(PromoterId promoterId, InstallmentStatus status);
}
