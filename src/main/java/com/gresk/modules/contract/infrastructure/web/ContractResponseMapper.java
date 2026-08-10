package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.FinancialTerms;
import com.gresk.modules.contract.domain.model.valueobject.PerformanceDetails;
import com.gresk.modules.contract.domain.port.out.ContractStats;
import com.gresk.modules.contract.infrastructure.web.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContractResponseMapper {

    public ContractResponse toResponse(Contract c) {
        return new ContractResponse(
                c.getId().toString(),
                c.getReferenceNumber(),
                c.getType(),
                c.getStatus(),
                toPartyResponse(c.getPartyA()),
                toPartyResponse(c.getPartyB()),
                toPerformanceResponse(c.getPerformanceDetails()),
                c.getFinancialTerms() != null ? c.getFinancialTerms().feeAmount()  : null,
                c.getFinancialTerms() != null ? c.getFinancialTerms().feeCurrency() : "EUR",
                c.getFinancialTerms() != null ? toPaymentTermResponses(c.getFinancialTerms()) : List.of(),
                c.getClauses().stream()
                        .map(cl -> new ContractResponse.ClauseResponse(cl.order(), cl.title(), cl.content()))
                        .toList(),
                c.getJurisdiction(),
                c.getContractCity(),
                c.getContractDate(),
                c.getLinkedEventId(),
                c.getLinkedArtistId(),
                c.getLinkedRiderId(),
                c.getSignedPdfAssetId(),
                c.getShareToken(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }

    public ContractSummaryResponse toSummary(Contract c) {
        return new ContractSummaryResponse(
                c.getId().toString(),
                c.getReferenceNumber(),
                c.getType(),
                c.getStatus(),
                c.getPartyB() != null ? c.getPartyB().name() : null,
                c.getFinancialTerms() != null ? c.getFinancialTerms().feeAmount()  : null,
                c.getFinancialTerms() != null ? c.getFinancialTerms().feeCurrency() : "EUR",
                c.getContractDate(),
                c.getUpdatedAt()
        );
    }

    public ContractStatsResponse toStatsResponse(ContractStats s) {
        return new ContractStatsResponse(
                s.draftCount(), s.sentCount(), s.signedCount(),
                s.archivedCount(), s.cancelledCount(),
                s.totalSignedFeeValue(), s.countByType()
        );
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ContractResponse.PartyResponse toPartyResponse(ContractParty party) {
        if (party == null) return null;
        return new ContractResponse.PartyResponse(
                party.name(), party.taxId(), party.address(),
                party.signatoryName(), party.signatoryRole(), party.email());
    }

    private ContractResponse.PerformanceResponse toPerformanceResponse(PerformanceDetails pd) {
        if (pd == null) return null;
        return new ContractResponse.PerformanceResponse(
                pd.venue(), pd.eventDate(), pd.durationMinutes(), pd.showTime());
    }

    private List<ContractResponse.PaymentTermResponse> toPaymentTermResponses(FinancialTerms ft) {
        if (ft.paymentTerms() == null) return List.of();
        return ft.paymentTerms().stream()
                .map(pt -> new ContractResponse.PaymentTermResponse(
                        pt.percentage(), pt.description(), pt.method(), pt.paid()))
                .toList();
    }
}
