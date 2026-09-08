package com.gresk.modules.quotation.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record AssignSupplierRequest(
        @NotBlank String supplierId,
        @NotBlank String catalogItemId
) {}
