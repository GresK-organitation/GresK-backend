package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.application.command.InitiateSignatureCommand;
import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.exception.SignatureEnvelopeAlreadyActiveException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractVersion;
import com.gresk.modules.contract.domain.model.SignatureEnvelope;
import com.gresk.modules.contract.domain.model.valueobject.AuditAction;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.Signer;
import com.gresk.modules.contract.domain.model.valueobject.SignerRole;
import com.gresk.modules.contract.domain.port.out.AuditTrailRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractVersionRepositoryPort;
import com.gresk.modules.contract.domain.port.out.SignatureEnvelopeRepositoryPort;
import com.gresk.modules.contract.domain.port.out.SignatureProviderPort;
import com.gresk.modules.contract.domain.service.DocumentHasher;
import com.gresk.modules.contract.infrastructure.pdf.ContractPdfGenerator;
import com.gresk.modules.contract.domain.model.AuditTrailEntry;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InitiateSignatureUseCase {

    private final ContractRepositoryPort           contractRepository;
    private final SignatureEnvelopeRepositoryPort  envelopeRepository;
    private final ContractVersionRepositoryPort    versionRepository;
    private final AuditTrailRepositoryPort         auditTrailRepository;
    private final SignatureProviderPort            signatureProvider;
    private final ContractPdfGenerator             pdfGenerator;

    public SignatureEnvelope execute(InitiateSignatureCommand cmd) {
        Contract contract = contractRepository.findById(ContractId.of(cmd.contractId()))
                .orElseThrow(() -> new ContractNotFoundException(cmd.contractId()));
        if (!contract.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new ContractNotOwnedException();
        }
        if (contract.getActiveSignatureEnvelopeId() != null) {
            throw new SignatureEnvelopeAlreadyActiveException();
        }

        // Snapshot de versión justo antes de enviar: congela el estado negociado.
        versionRepository.findCurrent(contract.getId()).ifPresent(v -> {
            v.markSuperseded();
            versionRepository.save(v);
        });
        ContractVersion version = ContractVersion.snapshot(contract, "Enviado a firma", cmd.promoterId());
        versionRepository.save(version);
        contract.advanceVersion();

        byte[] pdfBytes = pdfGenerator.generate(contract);
        String documentHash = DocumentHasher.sha256Hex(pdfBytes);

        List<Signer> signers = buildSigners(cmd, contract);
        SignatureEnvelope envelope = SignatureEnvelope.create(contract.getId(), cmd.provider(), signers, documentHash);

        var providerSigners = signers.stream()
                .map(s -> new SignatureProviderPort.SignatureProviderSigner(
                        s.signerId(), s.fullName(), s.email(), s.signOrder(), s.role().name()))
                .toList();
        var providerEnvelope = signatureProvider.createEnvelope(
                new SignatureProviderPort.SignatureProviderRequest(contract.getReferenceNumber(), pdfBytes, providerSigners));
        signatureProvider.sendEnvelope(providerEnvelope.providerEnvelopeId());
        envelope.markSent(providerEnvelope.providerEnvelopeId());

        if (contract.getStatus() == ContractStatus.DRAFT) {
            contract.send();
        }
        contract.attachSignatureEnvelope(envelope.getId());

        SignatureEnvelope saved = envelopeRepository.save(envelope);
        contractRepository.save(contract);

        auditTrailRepository.append(AuditTrailEntry.record(contract.getId(), AuditAction.SENT_FOR_SIGNATURE,
                cmd.promoterId(), null, null, Map.of("provider", cmd.provider().name(), "envelopeId", saved.getId().toString()),
                documentHash));

        return saved;
    }

    private List<Signer> buildSigners(InitiateSignatureCommand cmd, Contract contract) {
        if (cmd.signers() != null && !cmd.signers().isEmpty()) {
            List<Signer> signers = new ArrayList<>();
            int i = 1;
            for (var input : cmd.signers()) {
                signers.add(Signer.pending("signer-" + i++, input.role(), input.fullName(), input.email(), input.signOrder()));
            }
            return signers;
        }
        // Sin firmantes explícitos: derivar de partyA (promotor) y partyB (artista/agencia).
        List<Signer> defaults = new ArrayList<>();
        ContractParty a = contract.getPartyA();
        ContractParty b = contract.getPartyB();
        if (a != null) {
            defaults.add(Signer.pending("signer-1", SignerRole.PROMOTER,
                    a.signatoryName() != null ? a.signatoryName() : a.name(), a.email(), 1));
        }
        if (b != null) {
            defaults.add(Signer.pending("signer-2", SignerRole.ARTIST,
                    b.signatoryName() != null ? b.signatoryName() : b.name(), b.email(), 2));
        }
        return defaults;
    }
}
