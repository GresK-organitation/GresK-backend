package com.gresk.modules.rider.application.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateRiderCommand(
        String riderId,
        String promoterId,
        String name,
        Integer soundCheckDurationMinutes,
        String soundCheckNotes,
        List<StaffData> staff,
        StageDimensionsData stageDimensions,
        List<StageElementData> stageElements,
        String additionalNotes
) {
    public record StaffData(String role, String name) {}

    public record StageDimensionsData(
            BigDecimal widthMeters, BigDecimal depthMeters, BigDecimal minHeightMeters,
            Integer powerOutlets, Boolean hasDrumRiser) {}

    public record StageElementData(
            UUID elementId, String type,
            Double xPercent, Double yPercent,
            Integer rotationDegrees, String label) {}
}
