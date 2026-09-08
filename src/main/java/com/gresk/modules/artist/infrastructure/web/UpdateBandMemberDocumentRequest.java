package com.gresk.modules.artist.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateBandMemberDocumentRequest(
        @NotBlank String documentType,
        @NotBlank String documentNumber,
        @NotBlank String issuingCountry,
        LocalDate expiryDate
) {}
