package com.gresk.modules.rider.application.command;

public record CreateRiderCommand(
        String promoterId,
        String artistId,
        String name
) {}
