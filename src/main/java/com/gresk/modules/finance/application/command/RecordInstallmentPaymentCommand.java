package com.gresk.modules.finance.application.command;

import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordInstallmentPaymentCommand(
        String installmentId,
        String promoterId,
        LocalDate paidDate,
        String paymentMethod,
        WithholdingKind withholdingKind,
        BigDecimal withholdingRatePercentage,
        String exemptionReason
) {
}
