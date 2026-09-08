package com.gresk.modules.artist.application.command;

public record AddRosterMemberCommand(
        String artistId,
        String promoterId,
        String name,
        String role,
        String phone,
        String email,
        String billingLegalName,
        String billingTaxId,
        String billingAddress,
        String billingIban,
        boolean primary
) {}
