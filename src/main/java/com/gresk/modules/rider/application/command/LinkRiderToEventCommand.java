package com.gresk.modules.rider.application.command;

public record LinkRiderToEventCommand(
        String promoterId,
        String eventId,
        String riderId
) {}
