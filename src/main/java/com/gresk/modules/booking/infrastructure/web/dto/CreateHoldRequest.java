package com.gresk.modules.booking.infrastructure.web.dto;

import com.gresk.modules.booking.application.command.MilestoneBlueprintInput;
import com.gresk.modules.booking.application.command.TerritorialExclusivityInput;
import com.gresk.modules.booking.application.command.VenueRefInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record CreateHoldRequest(
        @NotBlank String artistId,
        @NotNull @Valid VenueRefInput venue,
        @NotNull Instant eventDate,
        @NotBlank String holdLevel,
        @NotNull Instant holdExpiresAt,
        List<MilestoneBlueprintInput> milestoneBlueprints,
        TerritorialExclusivityInput exclusivity,
        String notes,
        boolean forceIgnoreConflicts
) {
}
