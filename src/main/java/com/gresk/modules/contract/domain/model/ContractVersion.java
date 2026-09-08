package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.model.valueobject.ContractClause;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.FinancialTerms;
import com.gresk.modules.contract.domain.model.valueobject.PerformanceDetails;

import java.time.Instant;
import java.util.List;

/**
 * Snapshot inmutable de un estado de negociación. Se crea manualmente o justo antes de
 * enviar a firma; nunca se muta tras crearse.
 */
public final class ContractVersion {

    private final ContractVersionId      id;
    private final ContractId             contractId;
    private final int                    versionNumber;
    private final ContractParty          partyA;
    private final ContractParty          partyB;
    private final PerformanceDetails     performanceDetails;
    private final FinancialTerms         financialTerms;
    private final List<ContractClause>  clauses;
    private final String                 changeSummary;
    private final String                 createdBy;
    private final Instant                createdAt;
    private ContractVersionStatus         status;

    private ContractVersion(ContractVersionId id, ContractId contractId, int versionNumber,
                             ContractParty partyA, ContractParty partyB,
                             PerformanceDetails performanceDetails, FinancialTerms financialTerms,
                             List<ContractClause> clauses, String changeSummary,
                             String createdBy, Instant createdAt, ContractVersionStatus status) {
        this.id                 = id;
        this.contractId         = contractId;
        this.versionNumber      = versionNumber;
        this.partyA             = partyA;
        this.partyB             = partyB;
        this.performanceDetails = performanceDetails;
        this.financialTerms     = financialTerms;
        this.clauses            = clauses != null ? List.copyOf(clauses) : List.of();
        this.changeSummary      = changeSummary;
        this.createdBy          = createdBy;
        this.createdAt          = createdAt;
        this.status             = status;
    }

    public static ContractVersion snapshot(Contract contract, String changeSummary, String createdBy) {
        return new ContractVersion(ContractVersionId.generate(), contract.getId(), contract.nextVersionNumber(),
                contract.getPartyA(), contract.getPartyB(), contract.getPerformanceDetails(),
                contract.getFinancialTerms(), contract.getClauses(), changeSummary, createdBy,
                Instant.now(), ContractVersionStatus.CURRENT);
    }

    public static ContractVersion reconstitute(ContractVersionId id, ContractId contractId, int versionNumber,
                                                ContractParty partyA, ContractParty partyB,
                                                PerformanceDetails performanceDetails, FinancialTerms financialTerms,
                                                List<ContractClause> clauses, String changeSummary,
                                                String createdBy, Instant createdAt, ContractVersionStatus status) {
        return new ContractVersion(id, contractId, versionNumber, partyA, partyB, performanceDetails,
                financialTerms, clauses, changeSummary, createdBy, createdAt, status);
    }

    public void markSuperseded() { this.status = ContractVersionStatus.SUPERSEDED; }

    public ContractVersionId     getId()                 { return id; }
    public ContractId            getContractId()         { return contractId; }
    public int                   getVersionNumber()      { return versionNumber; }
    public ContractParty         getPartyA()             { return partyA; }
    public ContractParty         getPartyB()             { return partyB; }
    public PerformanceDetails    getPerformanceDetails() { return performanceDetails; }
    public FinancialTerms        getFinancialTerms()     { return financialTerms; }
    public List<ContractClause>  getClauses()            { return clauses; }
    public String                getChangeSummary()      { return changeSummary; }
    public String                getCreatedBy()          { return createdBy; }
    public Instant                getCreatedAt()          { return createdAt; }
    public ContractVersionStatus getStatus()             { return status; }
}
