package com.gresk.modules.contract.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdateContractRequest(
        ContractPartyRequest partyB,
        // Performance details
        String    perfVenue,
        LocalDate perfEventDate,
        Integer   perfDurationMinutes,
        String    perfShowTime,
        // Financial
        BigDecimal feeAmount,
        String     feeCurrency,
        List<PaymentTermRequest> paymentTerms,
        WithholdingTaxRequest withholdingTax,
        // Clauses
        List<ClauseRequest> clauses,
        // Administrative
        String    jurisdiction,
        String    contractCity,
        LocalDate contractDate,
        // Links
        UUID linkedEventId,
        UUID linkedArtistId,
        UUID linkedRiderId
) {
    public record PaymentTermRequest(BigDecimal percentage, String description, String method, boolean paid) {}
    public record ClauseRequest(int order, String title, String content) {}
    public record WithholdingTaxRequest(String type, BigDecimal ratePercentage, BigDecimal taxBase,
                                         BigDecimal withheldAmount, String exemptionReason) {}
}
