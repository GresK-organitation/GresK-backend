package com.gresk.modules.contract.application.command;

public record SignatureWebhookCommand(
        String providerEnvelopeId,
        String eventType,   // DELIVERED | SIGNER_SIGNED | DECLINED | VOIDED | EXPIRED
        String signerId,    // requerido para SIGNER_SIGNED/DECLINED puntual, opcional en el resto
        String ipAddress,
        String userAgent
) {}
