package com.gresk.modules.venue.domain.model.valueobject;

import java.time.LocalDate;

public record MunicipalLicense(
        String licenseNumber,
        LicenseType type,
        String issuingAuthority,
        LocalDate validFrom,
        LocalDate validUntil
) {

    public MunicipalLicense {
        if (licenseNumber == null || licenseNumber.isBlank()) {
            throw new IllegalArgumentException("MunicipalLicense licenseNumber must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("MunicipalLicense type must not be null");
        }
        if (issuingAuthority == null || issuingAuthority.isBlank()) {
            throw new IllegalArgumentException("MunicipalLicense issuingAuthority must not be blank");
        }
        if (validFrom == null || validUntil == null || !validUntil.isAfter(validFrom)) {
            throw new IllegalArgumentException("MunicipalLicense validUntil must be after validFrom");
        }
    }

    public boolean isValidOn(LocalDate date) {
        return !date.isBefore(validFrom) && !date.isAfter(validUntil);
    }
}
