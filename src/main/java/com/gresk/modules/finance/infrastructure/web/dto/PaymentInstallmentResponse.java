package com.gresk.modules.finance.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentInstallmentResponse(
        String id,
        String purpose,
        String linkedContractId,
        String linkedSettlementId,
        BigDecimal amount,
        String currency,
        String description,
        LocalDate dueDate,
        String status,
        LocalDate paidDate,
        String paymentMethod,
        WithholdingApplicationResponse withholding
) {
}
