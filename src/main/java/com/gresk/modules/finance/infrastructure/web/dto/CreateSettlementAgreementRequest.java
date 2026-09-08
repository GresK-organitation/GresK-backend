package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.domain.model.valueobject.SettlementDealType;

import java.math.BigDecimal;

public record CreateSettlementAgreementRequest(
        String linkedContractId,
        SettlementDealType dealType,
        BigDecimal guaranteedAmount,
        BigDecimal artistPercentage,
        BigDecimal revenueThreshold
) {
}
