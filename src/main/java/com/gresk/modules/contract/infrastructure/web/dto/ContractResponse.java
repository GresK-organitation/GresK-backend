package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ContractResponse(
        String         id,
        String         referenceNumber,
        ContractType   type,
        ContractStatus status,
        // Parties
        PartyResponse  partyA,
        PartyResponse  partyB,
        // Performance
        PerformanceResponse performanceDetails,
        // Financial
        BigDecimal     feeAmount,
        String         feeCurrency,
        List<PaymentTermResponse> paymentTerms,
        // Clauses
        List<ClauseResponse> clauses,
        // Admin
        String         jurisdiction,
        String         contractCity,
        LocalDate      contractDate,
        // Links
        UUID           linkedEventId,
        UUID           linkedArtistId,
        UUID           linkedRiderId,
        // Files & sharing
        String         signedPdfAssetId,
        String         shareToken,
        // Timestamps
        Instant        createdAt,
        Instant        updatedAt
) {
    public record PartyResponse(
            String name, String taxId, String address,
            String signatoryName, String signatoryRole, String email) {}

    public record PerformanceResponse(
            String venue, LocalDate eventDate, Integer durationMinutes, String showTime) {}

    public record PaymentTermResponse(
            BigDecimal percentage, String description, String method, boolean paid) {}

    public record ClauseResponse(int order, String title, String content) {}
}
