package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.valueobject.EnvelopeProvider;
import com.gresk.modules.contract.domain.model.valueobject.EnvelopeStatus;
import com.gresk.modules.contract.domain.model.valueobject.SignerRole;
import com.gresk.modules.contract.domain.model.valueobject.SignerStatus;

import java.time.Instant;
import java.util.List;

public record SignatureEnvelopeResponse(
        String           id,
        String           contractId,
        EnvelopeProvider provider,
        String           providerEnvelopeId,
        EnvelopeStatus   status,
        String           documentHash,
        List<SignerResponse> signers,
        Instant          sentAt,
        Instant          completedAt,
        Instant          createdAt
) {
    public record SignerResponse(String signerId, SignerRole role, String fullName, String email,
                                  int signOrder, SignerStatus status) {}
}
