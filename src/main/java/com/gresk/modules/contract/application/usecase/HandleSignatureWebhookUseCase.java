package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.application.command.SignatureWebhookCommand;
import com.gresk.modules.contract.domain.model.AuditTrailEntry;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.SignatureEnvelope;
import com.gresk.modules.contract.domain.model.valueobject.AuditAction;
import com.gresk.modules.contract.domain.port.out.AuditTrailRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.domain.port.out.SignatureEnvelopeRepositoryPort;
import com.gresk.modules.contract.infrastructure.event.ContractSignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Punto de entrada único para los webhooks de firma. El controller HTTP normaliza el
 * payload propietario del proveedor a este Command antes de invocar el caso de uso.
 * Idempotente: reprocesar el mismo evento no debe romper nada.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HandleSignatureWebhookUseCase {

    private final SignatureEnvelopeRepositoryPort envelopeRepository;
    private final ContractRepositoryPort           contractRepository;
    private final AuditTrailRepositoryPort         auditTrailRepository;
    private final ApplicationEventPublisher        eventPublisher;

    public void execute(SignatureWebhookCommand cmd) {
        SignatureEnvelope envelope = envelopeRepository.findByProviderEnvelopeId(cmd.providerEnvelopeId()).orElse(null);
        if (envelope == null) {
            log.warn("Signature webhook received for unknown providerEnvelopeId={}", cmd.providerEnvelopeId());
            return;
        }
        Contract contract = contractRepository.findById(envelope.getContractId()).orElse(null);
        if (contract == null) {
            log.warn("Signature webhook: contract {} not found for envelope {}", envelope.getContractId(), envelope.getId());
            return;
        }

        AuditAction action = applyEvent(cmd, envelope, contract);

        envelopeRepository.save(envelope);
        contractRepository.save(contract);

        auditTrailRepository.append(AuditTrailEntry.record(contract.getId(), action,
                "webhook:" + envelope.getProvider().name().toLowerCase(),
                cmd.ipAddress(), cmd.userAgent(),
                Map.of("eventType", cmd.eventType(), "envelopeId", envelope.getId().toString()),
                envelope.getDocumentHash()));

        if (envelope.isFullySigned()) {
            eventPublisher.publishEvent(new ContractSignedEvent(
                    this, contract.getId(), contract.getPromoterId(), contract.getType(),
                    contract.getLinkedEventId(), contract.getReferenceNumber(),
                    contract.getPartyB() != null ? contract.getPartyB().name() : null,
                    contract.getFeeAmount()
            ));
        }
    }

    private AuditAction applyEvent(SignatureWebhookCommand cmd, SignatureEnvelope envelope, Contract contract) {
        return switch (cmd.eventType()) {
            case "DELIVERED" -> {
                envelope.markDelivered();
                if (contract.getStatus() == ContractStatus.SENT) contract.markDelivered();
                yield AuditAction.ENVELOPE_DELIVERED;
            }
            case "SIGNER_SIGNED" -> {
                envelope.markSignerSigned(cmd.signerId());
                if (envelope.isFullySigned()) {
                    contract.sign();
                    yield AuditAction.SIGNED;
                }
                yield AuditAction.SIGNER_SIGNED;
            }
            case "DECLINED" -> {
                envelope.markDeclined(cmd.signerId());
                if (contract.getStatus() == ContractStatus.SENT || contract.getStatus() == ContractStatus.DELIVERED) {
                    contract.voidContract();
                }
                yield AuditAction.DECLINED;
            }
            case "VOIDED" -> {
                envelope.markVoided();
                if (contract.getStatus() == ContractStatus.SENT || contract.getStatus() == ContractStatus.DELIVERED) {
                    contract.voidContract();
                }
                yield AuditAction.VOIDED;
            }
            case "EXPIRED" -> {
                envelope.markExpired();
                if (contract.getStatus() == ContractStatus.SENT || contract.getStatus() == ContractStatus.DELIVERED) {
                    contract.voidContract();
                }
                yield AuditAction.VOIDED;
            }
            default -> throw new IllegalArgumentException("Unknown signature webhook eventType: " + cmd.eventType());
        };
    }
}
