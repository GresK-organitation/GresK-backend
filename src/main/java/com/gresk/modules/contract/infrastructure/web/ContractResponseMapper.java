package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.domain.model.AuditTrailEntry;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractVersion;
import com.gresk.modules.contract.domain.model.SignatureEnvelope;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.FinancialTerms;
import com.gresk.modules.contract.domain.model.valueobject.PerformanceDetails;
import com.gresk.modules.contract.domain.model.valueobject.Signer;
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
                toWithholdingTaxResponse(c.getFinancialTerms()),
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
                c.getActiveSignatureEnvelopeId() != null ? c.getActiveSignatureEnvelopeId().toString() : null,
                c.getCurrentVersionNumber(),
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
                s.draftCount(), s.sentCount(), s.deliveredCount(), s.signedCount(),
                s.archivedCount(), s.cancelledCount(), s.voidedCount(),
                s.totalSignedFeeValue(), s.countByType()
        );
    }

    public SignatureEnvelopeResponse toSignatureEnvelopeResponse(SignatureEnvelope e) {
        return new SignatureEnvelopeResponse(
                e.getId().toString(), e.getContractId().toString(), e.getProvider(),
                e.getProviderEnvelopeId(), e.getStatus(), e.getDocumentHash(),
                e.getSigners().stream().map(this::toSignerResponse).toList(),
                e.getSentAt(), e.getCompletedAt(), e.getCreatedAt());
    }

    private SignatureEnvelopeResponse.SignerResponse toSignerResponse(Signer s) {
        return new SignatureEnvelopeResponse.SignerResponse(
                s.signerId(), s.role(), s.fullName(), s.email(), s.signOrder(), s.status());
    }

    public ContractVersionResponse toVersionResponse(ContractVersion v) {
        return new ContractVersionResponse(
                v.getId().toString(), v.getVersionNumber(), v.getStatus(),
                toPartyResponse(v.getPartyA()), toPartyResponse(v.getPartyB()),
                toPerformanceResponse(v.getPerformanceDetails()),
                v.getFinancialTerms() != null ? v.getFinancialTerms().feeAmount() : null,
                v.getClauses().stream()
                        .map(cl -> new ContractResponse.ClauseResponse(cl.order(), cl.title(), cl.content()))
                        .toList(),
                v.getChangeSummary(), v.getCreatedBy(), v.getCreatedAt());
    }

    public AuditTrailEntryResponse toAuditTrailEntryResponse(AuditTrailEntry e) {
        return new AuditTrailEntryResponse(
                e.getId().toString(), e.getAction(), e.getActor(), e.getOccurredAt(),
                e.getIpAddress(), e.getMetadata(), e.getDocumentHash());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ContractResponse.PartyResponse toPartyResponse(ContractParty party) {
        if (party == null) return null;
        return new ContractResponse.PartyResponse(
                party.name(), party.taxId(), party.address(),
                party.signatoryName(), party.signatoryRole(), party.email(),
                party.country(), party.taxResident());
    }

    private ContractResponse.WithholdingTaxResponse toWithholdingTaxResponse(FinancialTerms ft) {
        if (ft == null || ft.withholdingTax() == null) return null;
        var wht = ft.withholdingTax();
        return new ContractResponse.WithholdingTaxResponse(
                wht.type().name(), wht.ratePercentage(), wht.taxBase(), wht.withheldAmount(), wht.exemptionReason());
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
