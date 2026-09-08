package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordInstallmentPaymentRequest(
        LocalDate paidDate,
        String paymentMethod,
        WithholdingKind withholdingKind,
        BigDecimal withholdingRatePercentage,
        String exemptionReason
) {
}
