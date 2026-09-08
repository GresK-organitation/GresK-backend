package com.gresk.modules.supplier.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RegisterSupplierRequest(
        @NotBlank String name,
        Set<String> specialties,
        String contactName,
        String contactEmail,
        String contactPhone,
        String serviceCity
) {}
