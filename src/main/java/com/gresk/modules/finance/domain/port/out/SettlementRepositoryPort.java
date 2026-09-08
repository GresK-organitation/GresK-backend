package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.model.SettlementAgreementId;
import com.gresk.modules.finance.domain.model.SettlementId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface SettlementRepositoryPort {
    Settlement save(Settlement settlement);
    Optional<Settlement> findById(SettlementId id);
    List<Settlement> findBySettlementAgreementId(SettlementAgreementId agreementId);
    List<Settlement> findByPromoterId(PromoterId promoterId);
}
