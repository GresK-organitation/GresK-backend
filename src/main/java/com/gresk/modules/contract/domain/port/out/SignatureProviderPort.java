package com.gresk.modules.contract.domain.port.out;

import java.util.List;

/**
 * Puerto intercambiable: SignaturitAdapter/DocuSignAdapter (futuros) lo implementan en
 * infraestructura. El dominio no conoce nombres de campos ni URLs de ningún proveedor.
 */
public interface SignatureProviderPort {

    SignatureProviderEnvelope createEnvelope(SignatureProviderRequest request);

    void sendEnvelope(String providerEnvelopeId);

    void voidEnvelope(String providerEnvelopeId, String reason);

    SignatureProviderEnvelope getEnvelopeStatus(String providerEnvelopeId);

    record SignatureProviderRequest(String contractReference, byte[] documentBytes,
                                     List<SignatureProviderSigner> signers) {}

    record SignatureProviderSigner(String signerId, String fullName, String email, int order, String role) {}

    record SignatureProviderEnvelope(String providerEnvelopeId, String status,
                                      List<SignatureProviderSignerStatus> signerStatuses) {}

    record SignatureProviderSignerStatus(String signerId, String status) {}
}
