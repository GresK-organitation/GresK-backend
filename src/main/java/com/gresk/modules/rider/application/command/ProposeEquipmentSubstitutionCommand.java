package com.gresk.modules.rider.application.command;

public record ProposeEquipmentSubstitutionCommand(
        String riderId,
        String promoterId,
        String lineItemId,
        String proposedAlternative,
        String proposedBy,
        String notes
) {}
