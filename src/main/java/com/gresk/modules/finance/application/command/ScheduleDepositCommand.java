package com.gresk.modules.finance.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScheduleDepositCommand(
        String promoterId,
        String linkedContractId,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        String description
) {
}
