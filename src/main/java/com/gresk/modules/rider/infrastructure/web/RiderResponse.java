package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.domain.model.valueobject.*;
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
        SoundSystemRequirements soundSystem,
        StageDimensions stageDimensions,
        HospitalityRequirements hospitality,
        TransportRequirements transport,
        List<StaffMember> staff,
        List<InputChannel> inputChannels,
        List<BacklineItem> backlineItems,
        List<StageElement> stageElements,
        String additionalNotes,
        Instant createdAt,
        Instant updatedAt
) {}
