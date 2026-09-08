package com.gresk.modules.rider.application.command;

public record UpdateHospitalityRiderCommand(
        String riderId,
        String promoterId,
        String name,
        String additionalNotes
) {}
