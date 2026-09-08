package com.gresk.modules.artist.application.command;

public record UpdateRosterMemberCommand(
        String rosterMemberId,
        String promoterId,
        String name,
        String role,
        String phone,
        String email,
        String billingLegalName,
        String billingTaxId,
        String billingAddress,
        String billingIban,
        boolean primary,
        boolean active
) {}
