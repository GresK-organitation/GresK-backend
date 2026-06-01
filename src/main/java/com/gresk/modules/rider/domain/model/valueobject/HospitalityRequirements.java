package com.gresk.modules.rider.domain.model.valueobject;

public record HospitalityRequirements(
        Integer dressingRoomCapacity,
        String cateringNotes,
        Integer waterBottlesOnStage,
        Integer passesCount
) {}
