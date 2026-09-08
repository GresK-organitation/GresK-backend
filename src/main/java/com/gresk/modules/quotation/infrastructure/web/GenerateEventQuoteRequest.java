package com.gresk.modules.quotation.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record GenerateEventQuoteRequest(
        @NotBlank String currency
) {}
