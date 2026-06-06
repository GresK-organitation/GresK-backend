package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.exception.ContractAlreadySignedException;
import com.gresk.modules.contract.domain.exception.InvalidContractStatusTransitionException;
import com.gresk.modules.contract.domain.model.valueobject.ContractClause;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.FinancialTerms;
import com.gresk.modules.contract.domain.model.valueobject.PerformanceDetails;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Contract {

    private final ContractId   id;
    private final PromoterId   promoterId;
    private final ContractType type;
    private final Instant      createdAt;

    private String               referenceNumber;
    private ContractStatus       status;
    private ContractParty        partyA;
    private ContractParty        partyB;
    private PerformanceDetails   performanceDetails;
    private FinancialTerms       financialTerms;
    private List<ContractClause> clauses;
    private String               jurisdiction;
    private String               contractCity;
    private LocalDate            contractDate;
    private UUID                 linkedEventId;
    private UUID                 linkedArtistId;
    private UUID                 linkedRiderId;
    private String               signedPdfAssetId;
    private String               shareToken;
    private Instant              updatedAt;

    private Contract(ContractId id, PromoterId promoterId, ContractType type,
                     String referenceNumber, ContractStatus status,
                     ContractParty partyA, ContractParty partyB,
                     PerformanceDetails performanceDetails, FinancialTerms financialTerms,
                     List<ContractClause> clauses, String jurisdiction,
                     String contractCity, LocalDate contractDate,
                     UUID linkedEventId, UUID linkedArtistId, UUID linkedRiderId,
                     String signedPdfAssetId, String shareToken,
                     Instant createdAt, Instant updatedAt) {
        this.id                 = id;
        this.promoterId         = promoterId;
        this.type               = type;
        this.referenceNumber    = referenceNumber;
        this.status             = status;
        this.partyA             = partyA;
        this.partyB             = partyB;
        this.performanceDetails = performanceDetails;
        this.financialTerms     = financialTerms;
        this.clauses            = clauses != null ? new ArrayList<>(clauses) : new ArrayList<>();
        this.jurisdiction       = jurisdiction;
        this.contractCity       = contractCity;
        this.contractDate       = contractDate;
        this.linkedEventId      = linkedEventId;
        this.linkedArtistId     = linkedArtistId;
        this.linkedRiderId      = linkedRiderId;
        this.signedPdfAssetId   = signedPdfAssetId;
        this.shareToken         = shareToken;
        this.createdAt          = createdAt;
        this.updatedAt          = updatedAt;
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    public static Contract create(ContractType type, PromoterId promoterId,
                                   ContractParty partyA, String referenceNumber) {
        Instant now = Instant.now();
        return new Contract(
                ContractId.generate(), promoterId, type,
                referenceNumber, ContractStatus.DRAFT,
                partyA, null, null, null,
                List.of(), null, null, null,
                null, null, null, null, null,
                now, now
        );
    }

    public static Contract reconstitute(
            ContractId id, PromoterId promoterId, ContractType type,
            String referenceNumber, ContractStatus status,
            ContractParty partyA, ContractParty partyB,
            PerformanceDetails performanceDetails, FinancialTerms financialTerms,
            List<ContractClause> clauses, String jurisdiction,
            String contractCity, LocalDate contractDate,
            UUID linkedEventId, UUID linkedArtistId, UUID linkedRiderId,
            String signedPdfAssetId, String shareToken,
            Instant createdAt, Instant updatedAt) {
        return new Contract(id, promoterId, type, referenceNumber, status,
                partyA, partyB, performanceDetails, financialTerms, clauses,
                jurisdiction, contractCity, contractDate,
                linkedEventId, linkedArtistId, linkedRiderId,
                signedPdfAssetId, shareToken, createdAt, updatedAt);
    }

    // ── Status transitions ────────────────────────────────────────────────────

    public void send() {
        if (status != ContractStatus.DRAFT) {
            throw new InvalidContractStatusTransitionException(
                    "Cannot send a contract in status: " + status);
        }
        this.status    = ContractStatus.SENT;
        this.updatedAt = Instant.now();
    }

    public void sign() {
        if (status != ContractStatus.SENT) {
            throw new InvalidContractStatusTransitionException(
                    "Cannot sign a contract in status: " + status);
        }
        this.status    = ContractStatus.SIGNED;
        this.updatedAt = Instant.now();
    }

    public void archive() {
        if (status != ContractStatus.SIGNED) {
            throw new InvalidContractStatusTransitionException(
                    "Cannot archive a contract in status: " + status);
        }
        this.status    = ContractStatus.ARCHIVED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status == ContractStatus.SIGNED || status == ContractStatus.ARCHIVED
                || status == ContractStatus.CANCELLED) {
            throw new InvalidContractStatusTransitionException(
                    "Cannot cancel a contract in status: " + status);
        }
        this.status    = ContractStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    // ── Share token ───────────────────────────────────────────────────────────

    public String generateShareToken() {
        if (shareToken == null) {
            shareToken = UUID.randomUUID().toString();
            updatedAt  = Instant.now();
        }
        return shareToken;
    }

    // ── Fluent setters ────────────────────────────────────────────────────────

    public Contract withPartyA(ContractParty partyA) {
        guardEditable();
        this.partyA    = partyA;
        this.updatedAt = Instant.now();
        return this;
    }

    public Contract withPartyB(ContractParty partyB) {
        guardEditable();
        this.partyB    = partyB;
        this.updatedAt = Instant.now();
        return this;
    }

    public Contract withPerformanceDetails(PerformanceDetails performanceDetails) {
        guardEditable();
        this.performanceDetails = performanceDetails;
        this.updatedAt          = Instant.now();
        return this;
    }

    public Contract withFinancialTerms(FinancialTerms financialTerms) {
        guardEditable();
        this.financialTerms = financialTerms;
        this.updatedAt      = Instant.now();
        return this;
    }

    public Contract withClauses(List<ContractClause> clauses) {
        guardEditable();
        this.clauses   = new ArrayList<>(clauses);
        this.updatedAt = Instant.now();
        return this;
    }

    public Contract withJurisdiction(String jurisdiction) {
        guardEditable();
        this.jurisdiction = jurisdiction;
        this.updatedAt    = Instant.now();
        return this;
    }

    public Contract withContractCity(String contractCity) {
        guardEditable();
        this.contractCity = contractCity;
        this.updatedAt    = Instant.now();
        return this;
    }

    public Contract withContractDate(LocalDate contractDate) {
        guardEditable();
        this.contractDate = contractDate;
        this.updatedAt    = Instant.now();
        return this;
    }

    public Contract withLinkedEventId(UUID linkedEventId) {
        guardEditable();
        this.linkedEventId = linkedEventId;
        this.updatedAt     = Instant.now();
        return this;
    }

    public Contract withLinkedArtistId(UUID linkedArtistId) {
        guardEditable();
        this.linkedArtistId = linkedArtistId;
        this.updatedAt      = Instant.now();
        return this;
    }

    public Contract withLinkedRiderId(UUID linkedRiderId) {
        guardEditable();
        this.linkedRiderId = linkedRiderId;
        this.updatedAt     = Instant.now();
        return this;
    }

    public Contract withSignedPdfAssetId(String signedPdfAssetId) {
        this.signedPdfAssetId = signedPdfAssetId;
        this.updatedAt        = Instant.now();
        return this;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void guardEditable() {
        if (status != ContractStatus.DRAFT) {
            throw new InvalidContractStatusTransitionException(
                    "Contract can only be edited in DRAFT status");
        }
    }

    public BigDecimal getFeeAmount() {
        return financialTerms != null ? financialTerms.feeAmount() : null;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public ContractId            getId()                  { return id; }
    public PromoterId            getPromoterId()          { return promoterId; }
    public ContractType          getType()                { return type; }
    public String                getReferenceNumber()     { return referenceNumber; }
    public ContractStatus        getStatus()              { return status; }
    public ContractParty         getPartyA()              { return partyA; }
    public ContractParty         getPartyB()              { return partyB; }
    public PerformanceDetails    getPerformanceDetails()  { return performanceDetails; }
    public FinancialTerms        getFinancialTerms()      { return financialTerms; }
    public List<ContractClause>  getClauses()             { return List.copyOf(clauses); }
    public String                getJurisdiction()        { return jurisdiction; }
    public String                getContractCity()        { return contractCity; }
    public LocalDate             getContractDate()        { return contractDate; }
    public UUID                  getLinkedEventId()       { return linkedEventId; }
    public UUID                  getLinkedArtistId()      { return linkedArtistId; }
    public UUID                  getLinkedRiderId()       { return linkedRiderId; }
    public String                getSignedPdfAssetId()    { return signedPdfAssetId; }
    public String                getShareToken()          { return shareToken; }
    public Instant               getCreatedAt()           { return createdAt; }
    public Instant               getUpdatedAt()           { return updatedAt; }
}
