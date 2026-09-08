package com.gresk.modules.show.infrastructure.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record RunViabilitySimulationRequest(
        List<CostLineItemRequest> costLineItems,
        @NotNull @Positive BigDecimal avgTicketPrice,
        @Min(0) @Max(100) int expectedSelloutPercent,
        @NotBlank String currency
) {}
