package com.gresk.modules.rider.infrastructure.web;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record RiderLineItemResponse(
        UUID id,
        String category,
        String description,
        int quantity,
        boolean required,
        Map<String, String> attributes,
        String fulfillmentSource,
        EquivalenceResponse equivalence,
        String notes
) {
    public record EquivalenceResponse(
            String requestedSpec,
            String proposedAlternative,
            String status,
            String proposedBy,
            String notes,
            Instant proposedAt,
            Instant decidedAt
    ) {}
}
