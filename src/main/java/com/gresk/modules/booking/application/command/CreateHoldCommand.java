package com.gresk.modules.booking.application.command;

import java.time.Instant;
import java.util.List;

public record CreateHoldCommand(
        String promoterId,
        String artistId,
        VenueRefInput venue,
        Instant eventDate,
        String holdLevel,
        Instant holdExpiresAt,
        List<MilestoneBlueprintInput> milestoneBlueprints,
        TerritorialExclusivityInput exclusivity,
        String notes,
        boolean forceIgnoreConflicts
) {
}
