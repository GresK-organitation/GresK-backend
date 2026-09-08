package com.gresk.modules.artist.application.command;

public record AddBandMemberCommand(
        String artistId,
        String promoterId,
        String name,
        String roleInBand
) {}
