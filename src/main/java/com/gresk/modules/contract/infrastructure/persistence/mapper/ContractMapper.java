package com.gresk.modules.contract.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.SignatureEnvelopeId;
import com.gresk.modules.contract.domain.model.valueobject.*;
import com.gresk.modules.contract.infrastructure.persistence.entity.ContractEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractMapper {

    private final ObjectMapper objectMapper;

    public Contract toDomain(ContractEntity e) {
        ContractParty partyA = null;
        if (e.getPartyAName() != null) {
            partyA = new ContractParty(
                    e.getPartyAName(), e.getPartyATaxId(), e.getPartyAAddress(),
                    e.getPartyASignatoryName(), e.getPartyASignatoryRole(), e.getPartyAEmail(),
                    e.getPartyACountry(), e.isPartyATaxResident());
        }

        ContractParty partyB = null;
        if (e.getPartyBName() != null) {
            partyB = new ContractParty(
                    e.getPartyBName(), e.getPartyBTaxId(), e.getPartyBAddress(),
                    e.getPartyBSignatoryName(), e.getPartyBSignatoryRole(), e.getPartyBEmail(),
                    e.getPartyBCountry(), e.isPartyBTaxResident());
        }

        PerformanceDetails performanceDetails = null;
        if (e.getPerfVenue() != null || e.getPerfEventDate() != null) {
            performanceDetails = new PerformanceDetails(
                    e.getPerfVenue(), e.getPerfEventDate(),
                    e.getPerfDurationMinutes(), e.getPerfShowTime());
        }

        FinancialTerms financialTerms = null;
        if (e.getFeeAmount() != null) {
            WithholdingTax wht = e.getWhtType() != null
                    ? new WithholdingTax(WithholdingTaxType.valueOf(e.getWhtType()), e.getWhtRatePercentage(),
                        e.getWhtTaxBase(), e.getWhtWithheldAmount(), e.getWhtExemptionReason())
                    : null;
            financialTerms = new FinancialTerms(
                    e.getFeeAmount(),
                    e.getFeeCurrency() != null ? e.getFeeCurrency() : "EUR",
                    deserializePaymentTerms(e.getPaymentTermsJson()),
                    wht);
        }

        return Contract.reconstitute(
                ContractId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                e.getType(),
                e.getReferenceNumber(),
                e.getStatus(),
                partyA, partyB,
                performanceDetails, financialTerms,
                deserializeClauses(e.getClausesJson()),
                e.getJurisdiction(), e.getContractCity(), e.getContractDate(),
                e.getLinkedEventId(), e.getLinkedArtistId(), e.getLinkedRiderId(),
                e.getSignedPdfAssetId(), e.getShareToken(),
                e.getCreatedAt(), e.getUpdatedAt(),
                e.getActiveSignatureEnvelopeId() != null ? SignatureEnvelopeId.of(e.getActiveSignatureEnvelopeId()) : null,
                e.getCurrentVersionNumber()
        );
    }

    public ContractEntity toEntity(Contract c) {
        return ContractEntity.builder()
                .id(c.getId().value())
                .promoterId(c.getPromoterId().value())
                .type(c.getType())
                .status(c.getStatus())
                .referenceNumber(c.getReferenceNumber())
                // Party A
                .partyAName(           c.getPartyA() != null ? c.getPartyA().name()          : null)
                .partyATaxId(          c.getPartyA() != null ? c.getPartyA().taxId()          : null)
                .partyAAddress(        c.getPartyA() != null ? c.getPartyA().address()        : null)
                .partyASignatoryName(  c.getPartyA() != null ? c.getPartyA().signatoryName()  : null)
                .partyASignatoryRole(  c.getPartyA() != null ? c.getPartyA().signatoryRole()  : null)
                .partyAEmail(          c.getPartyA() != null ? c.getPartyA().email()          : null)
                .partyACountry(        c.getPartyA() != null ? c.getPartyA().country()        : null)
                .partyATaxResident(    c.getPartyA() == null || c.getPartyA().taxResident())
                // Party B
                .partyBName(           c.getPartyB() != null ? c.getPartyB().name()          : null)
                .partyBTaxId(          c.getPartyB() != null ? c.getPartyB().taxId()          : null)
                .partyBAddress(        c.getPartyB() != null ? c.getPartyB().address()        : null)
                .partyBSignatoryName(  c.getPartyB() != null ? c.getPartyB().signatoryName()  : null)
                .partyBSignatoryRole(  c.getPartyB() != null ? c.getPartyB().signatoryRole()  : null)
                .partyBEmail(          c.getPartyB() != null ? c.getPartyB().email()          : null)
                .partyBCountry(        c.getPartyB() != null ? c.getPartyB().country()        : null)
                .partyBTaxResident(    c.getPartyB() == null || c.getPartyB().taxResident())
                // Performance
                .perfVenue(            c.getPerformanceDetails() != null ? c.getPerformanceDetails().venue()            : null)
                .perfEventDate(        c.getPerformanceDetails() != null ? c.getPerformanceDetails().eventDate()        : null)
                .perfDurationMinutes(  c.getPerformanceDetails() != null ? c.getPerformanceDetails().durationMinutes()  : null)
                .perfShowTime(         c.getPerformanceDetails() != null ? c.getPerformanceDetails().showTime()         : null)
                // Financial
                .feeAmount(            c.getFinancialTerms() != null ? c.getFinancialTerms().feeAmount()  : null)
                .feeCurrency(          c.getFinancialTerms() != null ? c.getFinancialTerms().feeCurrency() : "EUR")
                .paymentTermsJson(     c.getFinancialTerms() != null ? serializePaymentTerms(c.getFinancialTerms().paymentTerms()) : null)
                .clausesJson(          serializeClauses(c.getClauses()))
                // Withholding tax
                .whtType(              whtOrNone(c).type().name())
                .whtRatePercentage(    whtOrNone(c).ratePercentage())
                .whtTaxBase(           whtOrNone(c).taxBase())
                .whtWithheldAmount(    whtOrNone(c).withheldAmount())
                .whtExemptionReason(   whtOrNone(c).exemptionReason())
                // Admin
                .jurisdiction(c.getJurisdiction())
                .contractCity(c.getContractCity())
                .contractDate(c.getContractDate())
                // Links
                .linkedEventId(c.getLinkedEventId())
                .linkedArtistId(c.getLinkedArtistId())
                .linkedRiderId(c.getLinkedRiderId())
                // Files
                .signedPdfAssetId(c.getSignedPdfAssetId())
                .shareToken(c.getShareToken())
                .activeSignatureEnvelopeId(c.getActiveSignatureEnvelopeId() != null ? c.getActiveSignatureEnvelopeId().value() : null)
                .currentVersionNumber(c.getCurrentVersionNumber())
                // Timestamps
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private WithholdingTax whtOrNone(Contract c) {
        if (c.getFinancialTerms() == null || c.getFinancialTerms().withholdingTax() == null) {
            return WithholdingTax.none();
        }
        return c.getFinancialTerms().withholdingTax();
    }

    // ── JSON helpers ──────────────────────────────────────────────────────────

    private String serializeClauses(List<ContractClause> clauses) {
        if (clauses == null || clauses.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(clauses);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<ContractClause> deserializeClauses(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private String serializePaymentTerms(List<PaymentTerm> terms) {
        if (terms == null || terms.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(terms);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<PaymentTerm> deserializePaymentTerms(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
