package com.gresk.modules.finance.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScheduleDepositRequest(
        String linkedContractId,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        String description
) {
}
