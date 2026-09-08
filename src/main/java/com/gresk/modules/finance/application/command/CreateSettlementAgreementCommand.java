package com.gresk.modules.finance.application.command;

import com.gresk.modules.finance.domain.model.valueobject.SettlementDealType;

import java.math.BigDecimal;

public record CreateSettlementAgreementCommand(
        String promoterId,
        String linkedContractId,
        SettlementDealType dealType,
        BigDecimal guaranteedAmount,
        BigDecimal artistPercentage,
        BigDecimal revenueThreshold
) {
}
