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
        List<InputChannelData> inputChannels,
        SoundSystemData soundSystem,
        List<BacklineItemData> backlineItems,
        StageDimensionsData stageDimensions,
        List<StageElementData> stageElements,
        HospitalityData hospitality,
        TransportData transport,
        String additionalNotes
) {
    public record StaffData(String role, String name) {}

    public record InputChannelData(
            Integer channelNumber, String instrument,
            String microphone, String inserts, String notes) {}

    public record SoundSystemData(
            String consoleBrand, Integer consoleChannels,
            Integer monitorMixes, String paDescription, String processorNotes) {}

    public record BacklineItemData(
            String category, String description,
            String brand, String model, Boolean required) {}

    public record StageDimensionsData(
            BigDecimal widthMeters, BigDecimal depthMeters, BigDecimal minHeightMeters,
            Integer powerOutlets, Boolean hasDrumRiser) {}

    public record StageElementData(
            UUID elementId, String type,
            Double xPercent, Double yPercent,
            Integer rotationDegrees, String label) {}

    public record HospitalityData(
            Integer dressingRoomCapacity, String cateringNotes,
            Integer waterBottlesOnStage, Integer passesCount) {}

    public record TransportData(
            String vehicleType, Integer passengerCapacity, String notes) {}
}
