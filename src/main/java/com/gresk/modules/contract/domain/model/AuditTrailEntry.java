package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.model.valueobject.AuditAction;

import java.time.Instant;
import java.util.Map;

/** Entrada append-only. Nunca se edita ni se borra: es la prueba en caso de disputa legal. */
public final class AuditTrailEntry {

    private final AuditTrailEntryId    id;
    private final ContractId           contractId;
    private final AuditAction          action;
    private final String               actor;
    private final Instant              occurredAt;
    private final String               ipAddress;
    private final String               userAgent;
    private final Map<String, Object>  metadata;
    private final String               documentHash;

    private AuditTrailEntry(AuditTrailEntryId id, ContractId contractId, AuditAction action, String actor,
                             Instant occurredAt, String ipAddress, String userAgent,
                             Map<String, Object> metadata, String documentHash) {
        this.id           = id;
        this.contractId   = contractId;
        this.action       = action;
        this.actor        = actor;
        this.occurredAt   = occurredAt;
        this.ipAddress    = ipAddress;
        this.userAgent    = userAgent;
        this.metadata     = metadata != null ? Map.copyOf(metadata) : Map.of();
        this.documentHash = documentHash;
    }

    public static AuditTrailEntry record(ContractId contractId, AuditAction action, String actor,
                                          String ipAddress, String userAgent,
                                          Map<String, Object> metadata, String documentHash) {
        return new AuditTrailEntry(AuditTrailEntryId.generate(), contractId, action, actor,
                Instant.now(), ipAddress, userAgent, metadata, documentHash);
    }

    public static AuditTrailEntry reconstitute(AuditTrailEntryId id, ContractId contractId, AuditAction action,
                                                String actor, Instant occurredAt, String ipAddress,
                                                String userAgent, Map<String, Object> metadata, String documentHash) {
        return new AuditTrailEntry(id, contractId, action, actor, occurredAt, ipAddress, userAgent, metadata, documentHash);
    }

    public AuditTrailEntryId   getId()           { return id; }
    public ContractId          getContractId()   { return contractId; }
    public AuditAction         getAction()       { return action; }
    public String              getActor()        { return actor; }
    public Instant             getOccurredAt()   { return occurredAt; }
    public String              getIpAddress()    { return ipAddress; }
    public String              getUserAgent()    { return userAgent; }
    public Map<String, Object> getMetadata()     { return metadata; }
    public String              getDocumentHash() { return documentHash; }
}
