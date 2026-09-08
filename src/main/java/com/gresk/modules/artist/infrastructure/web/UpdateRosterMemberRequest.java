package com.gresk.modules.artist.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record UpdateRosterMemberRequest(
        @NotBlank String name,
        @NotBlank String role,
        @NotBlank String phone,
        String email,
        String billingLegalName,
        String billingTaxId,
        String billingAddress,
        String billingIban,
        boolean primary,
        boolean active
) {}
