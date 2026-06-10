package com.gresk.modules.email.application.command;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IngestEmailCommand(
        UUID promoterId,
        String externalMessageId,
        String externalThreadId,
        String fromAddress,
        String fromName,
        List<String> toAddresses,
        String subject,
        String bodyText,
        String bodyHtml,
        String rawHeadersJson,
        Instant receivedAt
) {}
