package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.exception.InvalidSignatureEnvelopeTransitionException;
import com.gresk.modules.contract.domain.model.valueobject.EnvelopeProvider;
import com.gresk.modules.contract.domain.model.valueobject.EnvelopeStatus;
import com.gresk.modules.contract.domain.model.valueobject.Signer;
import com.gresk.modules.contract.domain.model.valueobject.SignerStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate secundario: representa el "sobre" enviado al proveedor externo de firma.
 * Contract solo mantiene la referencia (activeSignatureEnvelopeId); el ciclo de vida
 * fino por firmante vive aquí para no ensuciar el aggregate root con estado del proveedor.
 */
public final class SignatureEnvelope {

    private final SignatureEnvelopeId id;
    private final ContractId          contractId;
    private final EnvelopeProvider    provider;
    private String                    providerEnvelopeId;
    private EnvelopeStatus            status;
    private String                    documentHash;
    private String                    certificateAssetId;
    private List<Signer>              signers;
    private Instant                   sentAt;
    private Instant                   completedAt;
    private final Instant             createdAt;
    private Instant                   updatedAt;

    private SignatureEnvelope(SignatureEnvelopeId id, ContractId contractId, EnvelopeProvider provider,
                               String providerEnvelopeId, EnvelopeStatus status, String documentHash,
                               String certificateAssetId, List<Signer> signers,
                               Instant sentAt, Instant completedAt, Instant createdAt, Instant updatedAt) {
        this.id                 = id;
        this.contractId         = contractId;
        this.provider           = provider;
        this.providerEnvelopeId = providerEnvelopeId;
        this.status             = status;
        this.documentHash       = documentHash;
        this.certificateAssetId = certificateAssetId;
        this.signers            = signers != null ? new ArrayList<>(signers) : new ArrayList<>();
        this.sentAt             = sentAt;
        this.completedAt        = completedAt;
        this.createdAt          = createdAt;
        this.updatedAt          = updatedAt;
    }

    public static SignatureEnvelope create(ContractId contractId, EnvelopeProvider provider,
                                            List<Signer> signers, String documentHash) {
        Instant now = Instant.now();
        return new SignatureEnvelope(SignatureEnvelopeId.generate(), contractId, provider,
                null, EnvelopeStatus.DRAFT, documentHash, null, signers, null, null, now, now);
    }

    public static SignatureEnvelope reconstitute(SignatureEnvelopeId id, ContractId contractId,
                                                  EnvelopeProvider provider, String providerEnvelopeId,
                                                  EnvelopeStatus status, String documentHash,
                                                  String certificateAssetId, List<Signer> signers,
                                                  Instant sentAt, Instant completedAt,
                                                  Instant createdAt, Instant updatedAt) {
        return new SignatureEnvelope(id, contractId, provider, providerEnvelopeId, status, documentHash,
                certificateAssetId, signers, sentAt, completedAt, createdAt, updatedAt);
    }

    // ── Transiciones ──────────────────────────────────────────────────────────

    public void markSent(String providerEnvelopeId) {
        if (status != EnvelopeStatus.DRAFT) {
            throw new InvalidSignatureEnvelopeTransitionException(
                    "Cannot send an envelope in status: " + status);
        }
        this.providerEnvelopeId = providerEnvelopeId;
        this.status             = EnvelopeStatus.SENT;
        this.sentAt             = Instant.now();
        this.updatedAt          = Instant.now();
    }

    public void markDelivered() {
        if (status != EnvelopeStatus.SENT) {
            throw new InvalidSignatureEnvelopeTransitionException(
                    "Cannot mark delivered an envelope in status: " + status);
        }
        this.status    = EnvelopeStatus.DELIVERED;
        this.updatedAt = Instant.now();
    }

    /** Marca un firmante como SIGNED; si todos los firmantes ya firmaron, el envelope pasa a SIGNED. */
    public void markSignerSigned(String signerId) {
        guardActive();
        this.signers = signers.stream()
                .map(s -> s.signerId().equals(signerId) ? s.withStatus(SignerStatus.SIGNED) : s)
                .toList();
        if (signers.stream().allMatch(s -> s.status() == SignerStatus.SIGNED)) {
            this.status      = EnvelopeStatus.SIGNED;
            this.completedAt = Instant.now();
        }
        this.updatedAt = Instant.now();
    }

    public void markDeclined(String signerId) {
        guardActive();
        this.signers = signers.stream()
                .map(s -> signerId != null && s.signerId().equals(signerId) ? s.withStatus(SignerStatus.DECLINED) : s)
                .toList();
        this.status    = EnvelopeStatus.DECLINED;
        this.updatedAt = Instant.now();
    }

    public void markVoided() {
        if (status == EnvelopeStatus.SIGNED) {
            throw new InvalidSignatureEnvelopeTransitionException("Cannot void a fully signed envelope");
        }
        this.status    = EnvelopeStatus.VOIDED;
        this.updatedAt = Instant.now();
    }

    public void markExpired() {
        guardActive();
        this.status    = EnvelopeStatus.EXPIRED;
        this.updatedAt = Instant.now();
    }

    public void attachCertificate(String certificateAssetId) {
        this.certificateAssetId = certificateAssetId;
        this.updatedAt          = Instant.now();
    }

    private void guardActive() {
        if (status == EnvelopeStatus.SIGNED || status == EnvelopeStatus.DECLINED
                || status == EnvelopeStatus.VOIDED || status == EnvelopeStatus.EXPIRED) {
            throw new InvalidSignatureEnvelopeTransitionException(
                    "Envelope is no longer active (status: " + status + ")");
        }
    }

    public boolean isFullySigned() { return status == EnvelopeStatus.SIGNED; }

    // ── Getters ───────────────────────────────────────────────────────────────

    public SignatureEnvelopeId getId()                  { return id; }
    public ContractId          getContractId()          { return contractId; }
    public EnvelopeProvider    getProvider()             { return provider; }
    public String              getProviderEnvelopeId()  { return providerEnvelopeId; }
    public EnvelopeStatus      getStatus()               { return status; }
    public String              getDocumentHash()        { return documentHash; }
    public String              getCertificateAssetId()  { return certificateAssetId; }
    public List<Signer>        getSigners()              { return List.copyOf(signers); }
    public Instant             getSentAt()               { return sentAt; }
    public Instant             getCompletedAt()          { return completedAt; }
    public Instant             getCreatedAt()            { return createdAt; }
    public Instant             getUpdatedAt()            { return updatedAt; }
}
