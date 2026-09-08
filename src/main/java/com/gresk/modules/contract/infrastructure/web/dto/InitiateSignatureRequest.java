package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.valueobject.EnvelopeProvider;
import com.gresk.modules.contract.domain.model.valueobject.SignerRole;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record InitiateSignatureRequest(
        @NotNull EnvelopeProvider provider,
        List<SignerRequest> signers
) {
    public record SignerRequest(SignerRole role, String fullName, String email, int signOrder) {}
}
