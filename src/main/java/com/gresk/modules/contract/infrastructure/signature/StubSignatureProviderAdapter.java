package com.gresk.modules.contract.infrastructure.signature;

import com.gresk.modules.contract.domain.port.out.SignatureProviderPort;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Simula el ciclo de vida de un proveedor de firma digital (Signaturit/DocuSign) sin
 * llamar a ninguna API real — no hay credenciales todavía. Deliberadamente NO
 * auto-progresa el envelope: el avance de estado se dispara posteando al webhook
 * (/api/v1/webhooks/signature/stub), para ejercitar de verdad la ruta de entrada que
 * sí importará cuando se conecte un proveedor real.
 */
@Component
@Profile({"dev", "test", "default"})
public class StubSignatureProviderAdapter implements SignatureProviderPort {

    private static final Logger log = LoggerFactory.getLogger(StubSignatureProviderAdapter.class);

    @PostConstruct
    public void init() {
        log.warn("StubSignatureProviderAdapter active — digital signatures are simulated");
    }

    @Override
    public SignatureProviderEnvelope createEnvelope(SignatureProviderRequest request) {
        String providerEnvelopeId = "STUB-" + UUID.randomUUID();
        log.info("Stub envelope created for reference {} -> {}", request.contractReference(), providerEnvelopeId);
        List<SignatureProviderSignerStatus> statuses = request.signers().stream()
                .map(s -> new SignatureProviderSignerStatus(s.signerId(), "created"))
                .toList();
        return new SignatureProviderEnvelope(providerEnvelopeId, "created", statuses);
    }

    @Override
    public void sendEnvelope(String providerEnvelopeId) {
        log.info("Stub envelope sent: {}", providerEnvelopeId);
    }

    @Override
    public void voidEnvelope(String providerEnvelopeId, String reason) {
        log.info("Stub envelope voided: {} ({})", providerEnvelopeId, reason);
    }

    @Override
    public SignatureProviderEnvelope getEnvelopeStatus(String providerEnvelopeId) {
        return new SignatureProviderEnvelope(providerEnvelopeId, "sent", List.of());
    }
}
