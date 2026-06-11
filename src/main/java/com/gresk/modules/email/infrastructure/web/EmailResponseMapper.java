package com.gresk.modules.email.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.infrastructure.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailResponseMapper {

    private final ObjectMapper objectMapper;

    public EmailResponse toResponse(EmailMessage m) {
        return new EmailResponse(
                m.getId().value(),
                m.getEventId(),
                m.getExternalThreadId(),
                m.getFromAddress(),
                m.getFromName(),
                m.getSubject(),
                m.getClassification() != null ? m.getClassification().name() : null,
                m.getClassificationConfidence(),
                m.getProcessingStatus().name(),
                m.getReceivedAt(),
                m.getProcessedAt());
    }

    public EmailDetailResponse toDetailResponse(EmailMessage m) {
        return new EmailDetailResponse(
                m.getId().value(),
                m.getEventId(),
                m.getExternalMessageId(),
                m.getExternalThreadId(),
                m.getFromAddress(),
                m.getFromName(),
                m.getToAddresses(),
                m.getSubject(),
                m.getBodyText(),
                m.getBodyHtml(),
                m.getClassification() != null ? m.getClassification().name() : null,
                m.getClassificationConfidence(),
                m.getProcessingStatus().name(),
                m.getReceivedAt(),
                m.getProcessedAt());
    }

    public RiderVersionResponse toResponse(EmailRiderVersion v) {
        return new RiderVersionResponse(
                v.getId().value(),
                v.getEventId(),
                v.getVersionNumber(),
                v.getSourceEmailId() != null ? v.getSourceEmailId().value() : null,
                toJson(v.getRiderDataJson()),
                toJson(v.getDiffFromPrevJson()),
                v.getCreatedBy().name(),
                v.getNotes(),
                v.getCreatedAt());
    }

    public DraftReplyResponse toResponse(EmailDraftReply d) {
        return new DraftReplyResponse(
                d.getId().value(),
                d.getEmailId().value(),
                d.getDraftType(),
                d.getSubject(),
                d.getBody(),
                d.getEditedBody(),
                d.getStatus().name(),
                d.getApprovedAt(),
                d.getSentAt(),
                d.getCreatedAt());
    }

    private JsonNode toJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }
}
