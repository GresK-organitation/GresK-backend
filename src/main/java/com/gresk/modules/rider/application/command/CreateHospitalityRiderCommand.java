package com.gresk.modules.rider.application.command;

public record CreateHospitalityRiderCommand(
        String promoterId,
        String artistId,
        String name
) {}
