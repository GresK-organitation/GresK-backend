package com.gresk.modules.contract.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdateContractCommand(
        String             contractId,
        String             promoterId,
        // Party B
        String             partyBName,
        String             partyBTaxId,
        String             partyBAddress,
        String             partyBSignatoryName,
        String             partyBSignatoryRole,
        String             partyBEmail,
        String             partyBCountry,
        Boolean            partyBTaxResident,
        // Performance details
        String             perfVenue,
        LocalDate          perfEventDate,
        Integer            perfDurationMinutes,
        String             perfShowTime,
        // Financial
        BigDecimal         feeAmount,
        String             feeCurrency,
        List<PaymentTermData> paymentTerms,
        WithholdingTaxData withholdingTax,
        // Clauses
        List<ClauseData>   clauses,
        // Administrative
        String             jurisdiction,
        String             contractCity,
        LocalDate          contractDate,
        // Links
        UUID               linkedEventId,
        UUID               linkedArtistId,
        UUID               linkedRiderId
) {
    public record PaymentTermData(BigDecimal percentage, String description, String method, boolean paid) {}
    public record ClauseData(int order, String title, String content) {}
    public record WithholdingTaxData(String type, BigDecimal ratePercentage, BigDecimal taxBase,
                                      BigDecimal withheldAmount, String exemptionReason) {}
}
