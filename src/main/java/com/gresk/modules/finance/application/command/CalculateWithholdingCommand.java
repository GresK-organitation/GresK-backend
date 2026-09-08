package com.gresk.modules.finance.application.command;

import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;

import java.math.BigDecimal;

public record CalculateWithholdingCommand(
        BigDecimal taxBaseAmount,
        String currency,
        WithholdingKind withholdingKind,
        BigDecimal ratePercentageOverride,
        String exemptionReason
) {
}
