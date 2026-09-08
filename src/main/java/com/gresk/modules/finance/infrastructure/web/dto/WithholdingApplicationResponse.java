package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;

import java.math.BigDecimal;

public record WithholdingApplicationResponse(
        WithholdingKind kind,
        BigDecimal ratePercentage,
        BigDecimal taxBaseAmount,
        BigDecimal withheldAmount,
        String currency,
        String exemptionReason
) {
}
