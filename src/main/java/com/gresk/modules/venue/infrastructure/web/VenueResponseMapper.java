package com.gresk.modules.venue.infrastructure.web;

import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.valueobject.CurfewPolicy;
import com.gresk.modules.venue.domain.model.valueobject.EvacuationPlan;
import com.gresk.modules.venue.domain.model.valueobject.LoadingDockSpec;
import com.gresk.modules.venue.infrastructure.web.dto.VenueResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class VenueResponseMapper {

    public VenueResponse toResponse(Venue v) {
        CurfewPolicy curfew = v.getCurfewPolicy();
        EvacuationPlan evac = v.getEvacuationPlan();
        LoadingDockSpec dock = v.getLoadingDockSpec();
        LocalDate today = LocalDate.now();

        return new VenueResponse(
                v.getId().toString(),
                v.getOwnerId().toString(),
                v.getName(),
                new VenueResponse.AddressResponse(v.getAddress().street(), v.getAddress().city().value(), v.getAddress().country()),
                v.getCapacityConfigurations().stream()
                        .map(c -> new VenueResponse.CapacityConfigurationResponse(c.code(), c.label(), c.layout(), c.maxCapacity()))
                        .toList(),
                curfew == null ? null : new VenueResponse.CurfewPolicyResponse(
                        curfew.hardCutoff(), curfew.maxDecibels(), curfew.restrictedDays(), curfew.notes()),
                evac == null ? null : new VenueResponse.EvacuationPlanResponse(
                        evac.documentAsset().value(), evac.certifiedCapacity(), evac.lastReviewedAt(), evac.reviewedBy()),
                v.getLicenses().stream()
                        .map(l -> new VenueResponse.MunicipalLicenseResponse(
                                l.licenseNumber(), l.type(), l.issuingAuthority(), l.validFrom(), l.validUntil(),
                                l.isValidOn(today)))
                        .toList(),
                dock == null ? null : new VenueResponse.LoadingDockSpecResponse(
                        dock.accessHeightMeters(), dock.accessWidthMeters(), dock.maxVehicleWeightKg(),
                        dock.windowStart(), dock.windowEnd(), dock.dockCount(), dock.notes()),
                v.isActive(),
                v.getCreatedAt(),
                v.getUpdatedAt()
        );
    }
}
