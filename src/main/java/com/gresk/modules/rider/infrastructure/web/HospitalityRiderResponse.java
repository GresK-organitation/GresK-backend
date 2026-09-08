package com.gresk.modules.rider.infrastructure.web;

import java.time.Instant;
import java.util.List;

public record HospitalityRiderResponse(
        String id,
        String artistId,
        String promoterId,
        String name,
        String status,
        int version,
        String shareToken,
        List<RiderLineItemResponse> lineItems,
        String additionalNotes,
        Instant createdAt,
        Instant updatedAt
) {}
