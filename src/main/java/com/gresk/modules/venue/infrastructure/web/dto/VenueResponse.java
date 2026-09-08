package com.gresk.modules.venue.infrastructure.web.dto;

import com.gresk.modules.venue.domain.model.valueobject.CapacityLayout;
import com.gresk.modules.venue.domain.model.valueobject.LicenseType;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public record VenueResponse(
        String  id,
        String  ownerId,
        String  name,
        AddressResponse address,
        List<CapacityConfigurationResponse> capacityConfigurations,
        CurfewPolicyResponse   curfewPolicy,
        EvacuationPlanResponse evacuationPlan,
        List<MunicipalLicenseResponse> licenses,
        LoadingDockSpecResponse loadingDockSpec,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public record AddressResponse(String street, String city, String country) {}

    public record CapacityConfigurationResponse(String code, String label, CapacityLayout layout, int maxCapacity) {}

    public record CurfewPolicyResponse(LocalTime hardCutoff, Integer maxDecibels,
                                        Set<DayOfWeek> restrictedDays, String notes) {}

    public record EvacuationPlanResponse(String documentAssetId, int certifiedCapacity,
                                          LocalDate lastReviewedAt, String reviewedBy) {}

    public record MunicipalLicenseResponse(String licenseNumber, LicenseType type, String issuingAuthority,
                                            LocalDate validFrom, LocalDate validUntil, boolean currentlyValid) {}

    public record LoadingDockSpecResponse(double accessHeightMeters, double accessWidthMeters,
                                           double maxVehicleWeightKg, LocalTime windowStart, LocalTime windowEnd,
                                           int dockCount, String notes) {}
}
