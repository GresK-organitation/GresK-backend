package com.gresk.modules.email.infrastructure.web.dto;

import java.time.Instant;
import java.util.UUID;

public record DraftReplyResponse(
        UUID id,
        UUID emailId,
        String draftType,
        String subject,
        String body,
        String editedBody,
        String status,
        Instant approvedAt,
        Instant sentAt,
        Instant createdAt
) {}
