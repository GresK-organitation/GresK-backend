package com.gresk.modules.artist.application.command;

import java.time.LocalDate;

public record UpdateBandMemberDocumentCommand(
        String bandMemberId,
        String promoterId,
        String documentType,
        String documentNumber,
        String issuingCountry,
        LocalDate expiryDate
) {}
