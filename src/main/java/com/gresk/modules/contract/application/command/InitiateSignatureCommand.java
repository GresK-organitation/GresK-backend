package com.gresk.modules.contract.application.command;

import com.gresk.modules.contract.domain.model.valueobject.EnvelopeProvider;
import com.gresk.modules.contract.domain.model.valueobject.SignerRole;

import java.util.List;

public record InitiateSignatureCommand(
        String          contractId,
        String          promoterId,
        EnvelopeProvider provider,
        List<SignerInput> signers   // vacío/null = derivar de partyA/partyB
) {
    public record SignerInput(SignerRole role, String fullName, String email, int signOrder) {}
}
