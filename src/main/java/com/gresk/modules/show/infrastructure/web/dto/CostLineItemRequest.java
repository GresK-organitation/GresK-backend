package com.gresk.modules.show.infrastructure.web.dto;

import com.gresk.modules.show.domain.model.valueobject.CostCategory;
import com.gresk.modules.show.domain.model.valueobject.CostNature;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CostLineItemRequest(
        @NotNull  CostCategory category,
        @NotBlank String label,
        @NotNull @PositiveOrZero BigDecimal amount,
        @NotNull  CostNature nature
) {}
