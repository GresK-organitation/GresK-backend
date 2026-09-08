package com.gresk.modules.show.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record SettleShowRequest(
        @Min(0) int actualAttendance,
        @NotNull @PositiveOrZero BigDecimal actualRevenue,
        @NotNull @PositiveOrZero BigDecimal actualCosts
) {}
