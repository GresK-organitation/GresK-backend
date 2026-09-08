package com.gresk.modules.venue.application.command;

import com.gresk.modules.venue.domain.model.valueobject.LicenseType;

import java.time.LocalDate;

public record UpdateMunicipalLicenseCommand(
        String venueId,
        String promoterId,
        String licenseNumber,
        LicenseType type,
        String issuingAuthority,
        LocalDate validFrom,
        LocalDate validUntil
) {
}
