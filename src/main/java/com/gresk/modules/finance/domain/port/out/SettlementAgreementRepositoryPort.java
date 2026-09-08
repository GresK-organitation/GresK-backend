package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.finance.domain.model.SettlementAgreement;
import com.gresk.modules.finance.domain.model.SettlementAgreementId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementAgreementRepositoryPort {
    SettlementAgreement save(SettlementAgreement agreement);
    Optional<SettlementAgreement> findById(SettlementAgreementId id);
    Optional<SettlementAgreement> findActiveByLinkedContractId(UUID linkedContractId);
    List<SettlementAgreement> findByPromoterId(PromoterId promoterId);
}
