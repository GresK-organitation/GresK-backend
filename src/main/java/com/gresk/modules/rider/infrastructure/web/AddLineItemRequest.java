package com.gresk.modules.rider.infrastructure.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record AddLineItemRequest(
        @NotBlank String category,
        @NotBlank String description,
        @Min(1) int quantity,
        boolean required,
        Map<String, String> attributes,
        String notes
) {}
