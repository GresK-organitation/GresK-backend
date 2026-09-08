package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.application.command.SignatureWebhookCommand;
import com.gresk.modules.contract.application.usecase.HandleSignatureWebhookUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Punto de entrada único para los webhooks de firma digital. El payload es nuestro
 * propio formato normalizado (no hay proveedor real conectado todavía); cuando se
 * conecte Signaturit/DocuSign, su formato se traduce a este mismo Command antes de
 * invocar el caso de uso, sin que este controller cambie.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/signature")
@RequiredArgsConstructor
public class SignatureWebhookController {

    private final HandleSignatureWebhookUseCase handleWebhookUseCase;

    @Value("${gresk.contract.signature-webhook-token:}")
    private String expectedToken;

    @PostMapping("/{provider}")
    public ResponseEntity<Void> receive(@PathVariable String provider,
                                        @RequestParam(value = "token", required = false) String token,
                                        @RequestBody SignatureWebhookRequest request,
                                        HttpServletRequest httpRequest) {
        if (!isValidToken(token)) {
            log.warn("Signature webhook rejected: invalid token (provider={})", provider);
            return ResponseEntity.status(401).build();
        }

        try {
            handleWebhookUseCase.execute(new SignatureWebhookCommand(
                    request.providerEnvelopeId(), request.eventType(), request.signerId(),
                    httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")));
        } catch (Exception e) {
            // 2xx igualmente: un error de parseo/estado no se arregla con redeliveries infinitas
            log.error("Signature webhook: error processing event: {}", e.getMessage(), e);
        }

        return ResponseEntity.noContent().build();
    }

    private boolean isValidToken(String token) {
        return expectedToken != null && !expectedToken.isBlank() && expectedToken.equals(token);
    }

    public record SignatureWebhookRequest(String providerEnvelopeId, String eventType,
                                          String signerId, String occurredAt) {}
}
