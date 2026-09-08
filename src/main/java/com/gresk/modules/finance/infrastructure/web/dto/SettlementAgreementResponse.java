package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.domain.model.valueobject.SettlementDealType;

import java.math.BigDecimal;

public record SettlementAgreementResponse(
        String id,
        String linkedContractId,
        String linkedEventId,
        SettlementDealType dealType,
        BigDecimal guaranteedAmount,
        BigDecimal artistPercentage,
        BigDecimal revenueThreshold,
        String currency,
        BigDecimal contractFeeSnapshot,
        String artistNameSnapshot,
        String status
) {
}
