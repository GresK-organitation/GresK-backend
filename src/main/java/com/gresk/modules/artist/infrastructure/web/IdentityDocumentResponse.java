package com.gresk.modules.artist.infrastructure.web;

import java.time.LocalDate;

public record IdentityDocumentResponse(
        String type,
        String documentNumber,
        String issuingCountry,
        LocalDate expiryDate
) {}
