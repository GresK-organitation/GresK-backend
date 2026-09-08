package com.gresk.modules.venue.infrastructure.web;

import com.gresk.modules.venue.application.command.AddCapacityConfigurationCommand;
import com.gresk.modules.venue.application.command.RegisterVenueCommand;
import com.gresk.modules.venue.application.command.UpdateCurfewPolicyCommand;
import com.gresk.modules.venue.application.command.UpdateEvacuationPlanCommand;
import com.gresk.modules.venue.application.command.UpdateLoadingDockSpecCommand;
import com.gresk.modules.venue.application.command.UpdateMunicipalLicenseCommand;
import com.gresk.modules.venue.application.port.in.AddCapacityConfigurationUseCase;
import com.gresk.modules.venue.application.port.in.GetVenueUseCase;
import com.gresk.modules.venue.application.port.in.ListVenuesUseCase;
import com.gresk.modules.venue.application.port.in.RegisterVenueUseCase;
import com.gresk.modules.venue.application.port.in.UpdateCurfewPolicyUseCase;
import com.gresk.modules.venue.application.port.in.UpdateEvacuationPlanUseCase;
import com.gresk.modules.venue.application.port.in.UpdateLoadingDockSpecUseCase;
import com.gresk.modules.venue.application.port.in.UpdateMunicipalLicenseUseCase;
import com.gresk.modules.venue.infrastructure.web.dto.AddCapacityConfigurationRequest;
import com.gresk.modules.venue.infrastructure.web.dto.RegisterVenueRequest;
import com.gresk.modules.venue.infrastructure.web.dto.UpdateCurfewPolicyRequest;
import com.gresk.modules.venue.infrastructure.web.dto.UpdateEvacuationPlanRequest;
import com.gresk.modules.venue.infrastructure.web.dto.UpdateLoadingDockSpecRequest;
import com.gresk.modules.venue.infrastructure.web.dto.UpdateMunicipalLicenseRequest;
import com.gresk.modules.venue.infrastructure.web.dto.VenueResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
public class VenueController {

    private final RegisterVenueUseCase registerVenueUseCase;
    private final AddCapacityConfigurationUseCase addCapacityConfigurationUseCase;
    private final UpdateCurfewPolicyUseCase updateCurfewPolicyUseCase;
    private final UpdateEvacuationPlanUseCase updateEvacuationPlanUseCase;
    private final UpdateMunicipalLicenseUseCase updateMunicipalLicenseUseCase;
    private final UpdateLoadingDockSpecUseCase updateLoadingDockSpecUseCase;
    private final GetVenueUseCase getVenueUseCase;
    private final ListVenuesUseCase listVenuesUseCase;
    private final VenueResponseMapper mapper;

    @PostMapping
    public ResponseEntity<VenueResponse> register(@AuthenticationPrincipal String promoterId,
                                                   @Valid @RequestBody RegisterVenueRequest request) {
        var venue = registerVenueUseCase.execute(new RegisterVenueCommand(
                promoterId, request.name(), request.street(), request.city(), request.country()));
        return ResponseEntity.created(URI.create("/api/v1/venues/" + venue.getId()))
                .body(mapper.toResponse(venue));
    }

    @GetMapping
    public List<VenueResponse> list(@AuthenticationPrincipal String promoterId) {
        return listVenuesUseCase.execute(promoterId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{venueId}")
    public VenueResponse getById(@AuthenticationPrincipal String promoterId, @PathVariable String venueId) {
        return mapper.toResponse(getVenueUseCase.execute(venueId, promoterId));
    }

    @PostMapping("/{venueId}/capacity-configurations")
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse addCapacityConfiguration(@AuthenticationPrincipal String promoterId, @PathVariable String venueId,
                                                   @Valid @RequestBody AddCapacityConfigurationRequest request) {
        var command = new AddCapacityConfigurationCommand(
                venueId, promoterId, request.code(), request.label(), request.layout(), request.maxCapacity());
        return mapper.toResponse(addCapacityConfigurationUseCase.execute(command));
    }

    @PutMapping("/{venueId}/curfew")
    public VenueResponse updateCurfew(@AuthenticationPrincipal String promoterId, @PathVariable String venueId,
                                       @Valid @RequestBody UpdateCurfewPolicyRequest request) {
        var command = new UpdateCurfewPolicyCommand(venueId, promoterId,
                request.hardCutoff(), request.maxDecibels(), request.restrictedDays(), request.notes());
        return mapper.toResponse(updateCurfewPolicyUseCase.execute(command));
    }

    @PutMapping("/{venueId}/evacuation-plan")
    public VenueResponse updateEvacuationPlan(@AuthenticationPrincipal String promoterId, @PathVariable String venueId,
                                               @Valid @RequestBody UpdateEvacuationPlanRequest request) {
        var command = new UpdateEvacuationPlanCommand(venueId, promoterId,
                request.documentAssetId(), request.certifiedCapacity(), request.lastReviewedAt(), request.reviewedBy());
        return mapper.toResponse(updateEvacuationPlanUseCase.execute(command));
    }

    @PutMapping("/{venueId}/license")
    public VenueResponse updateLicense(@AuthenticationPrincipal String promoterId, @PathVariable String venueId,
                                        @Valid @RequestBody UpdateMunicipalLicenseRequest request) {
        var command = new UpdateMunicipalLicenseCommand(venueId, promoterId, request.licenseNumber(),
                request.type(), request.issuingAuthority(), request.validFrom(), request.validUntil());
        return mapper.toResponse(updateMunicipalLicenseUseCase.execute(command));
    }

    @PutMapping("/{venueId}/loading-dock")
    public VenueResponse updateLoadingDock(@AuthenticationPrincipal String promoterId, @PathVariable String venueId,
                                            @Valid @RequestBody UpdateLoadingDockSpecRequest request) {
        var command = new UpdateLoadingDockSpecCommand(venueId, promoterId, request.accessHeightMeters(),
                request.accessWidthMeters(), request.maxVehicleWeightKg(), request.windowStart(),
                request.windowEnd(), request.dockCount(), request.notes());
        return mapper.toResponse(updateLoadingDockSpecUseCase.execute(command));
    }
}
