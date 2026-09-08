package com.gresk.modules.venue.infrastructure.web.dto;

import com.gresk.modules.venue.domain.model.valueobject.LicenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateMunicipalLicenseRequest(
        @NotBlank String licenseNumber,
        @NotNull  LicenseType type,
        @NotBlank String issuingAuthority,
        @NotNull  LocalDate validFrom,
        @NotNull  LocalDate validUntil
) {}
