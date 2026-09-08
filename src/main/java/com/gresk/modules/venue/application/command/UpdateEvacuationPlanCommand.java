package com.gresk.modules.venue.application.command;

import java.time.LocalDate;

public record UpdateEvacuationPlanCommand(
        String venueId,
        String promoterId,
        String documentAssetId,
        int certifiedCapacity,
        LocalDate lastReviewedAt,
        String reviewedBy
) {
}
