package com.gresk.modules.artist.infrastructure.web;

public record RosterMemberResponse(
        String id,
        String artistId,
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
