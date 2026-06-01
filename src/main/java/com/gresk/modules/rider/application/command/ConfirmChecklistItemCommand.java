package com.gresk.modules.rider.application.command;

public record ConfirmChecklistItemCommand(
        String promoterId,
        String eventId,
        String entryId,
        String notes
) {}
