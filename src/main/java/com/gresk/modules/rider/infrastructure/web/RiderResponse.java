package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.domain.model.valueobject.StageDimensions;
import com.gresk.modules.rider.domain.model.valueobject.StageElement;
import com.gresk.modules.rider.domain.model.valueobject.StaffMember;
import java.time.Instant;
import java.util.List;

public record RiderResponse(
        String id,
        String artistId,
        String promoterId,
        String name,
        String status,
        int version,
        String shareToken,
        Integer soundCheckDurationMinutes,
        String soundCheckNotes,
        StageDimensions stageDimensions,
        List<StageElement> stageElements,
        List<StaffMember> staff,
        List<RiderLineItemResponse> lineItems,
        String additionalNotes,
        Instant createdAt,
        Instant updatedAt
) {}
