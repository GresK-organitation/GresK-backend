package com.gresk.modules.contract.domain.model.valueobject;

import java.time.Instant;

public record Signer(
        String       signerId,
        SignerRole   role,
        String       fullName,
        String       email,
        int          signOrder,
        SignerStatus status,
        String       providerSignerId,
        Instant      signedAt
) {
    public static Signer pending(String signerId, SignerRole role, String fullName,
                                  String email, int signOrder) {
        return new Signer(signerId, role, fullName, email, signOrder, SignerStatus.PENDING, null, null);
    }

    public Signer withStatus(SignerStatus newStatus) {
        Instant signedAt = newStatus == SignerStatus.SIGNED ? Instant.now() : this.signedAt;
        return new Signer(signerId, role, fullName, email, signOrder, newStatus, providerSignerId, signedAt);
    }

    public Signer withProviderSignerId(String providerSignerId) {
        return new Signer(signerId, role, fullName, email, signOrder, status, providerSignerId, signedAt);
    }
}
