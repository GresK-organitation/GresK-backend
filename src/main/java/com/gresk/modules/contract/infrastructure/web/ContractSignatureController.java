package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.application.command.InitiateSignatureCommand;
import com.gresk.modules.contract.application.usecase.GetSignatureEnvelopeUseCase;
import com.gresk.modules.contract.application.usecase.InitiateSignatureUseCase;
import com.gresk.modules.contract.infrastructure.web.dto.InitiateSignatureRequest;
import com.gresk.modules.contract.infrastructure.web.dto.SignatureEnvelopeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts/{contractId}/signature")
@RequiredArgsConstructor
public class ContractSignatureController {

    private final InitiateSignatureUseCase     initiateUseCase;
    private final GetSignatureEnvelopeUseCase  getEnvelopeUseCase;
    private final ContractResponseMapper       mapper;

    @PostMapping("/envelopes")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SignatureEnvelopeResponse> initiate(
            @PathVariable String contractId,
            @RequestBody @Valid InitiateSignatureRequest request,
            @AuthenticationPrincipal String promoterId) {

        var signers = request.signers() == null ? java.util.List.<InitiateSignatureCommand.SignerInput>of() :
                request.signers().stream()
                        .map(s -> new InitiateSignatureCommand.SignerInput(s.role(), s.fullName(), s.email(), s.signOrder()))
                        .toList();

        var envelope = initiateUseCase.execute(new InitiateSignatureCommand(
                contractId, promoterId, request.provider(), signers));

        return ResponseEntity.status(201).body(mapper.toSignatureEnvelopeResponse(envelope));
    }

    @GetMapping("/envelopes/{envelopeId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<SignatureEnvelopeResponse> get(
            @PathVariable String contractId,
            @PathVariable String envelopeId,
            @AuthenticationPrincipal String promoterId) {

        var envelope = getEnvelopeUseCase.execute(contractId, envelopeId, promoterId);
        return ResponseEntity.ok(mapper.toSignatureEnvelopeResponse(envelope));
    }
}
