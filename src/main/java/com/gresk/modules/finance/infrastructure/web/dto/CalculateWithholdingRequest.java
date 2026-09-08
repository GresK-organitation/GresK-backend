package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;

import java.math.BigDecimal;

public record CalculateWithholdingRequest(
        BigDecimal taxBaseAmount,
        String currency,
        WithholdingKind withholdingKind,
        BigDecimal ratePercentageOverride,
        String exemptionReason
) {
}
