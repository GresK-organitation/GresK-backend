package com.gresk.modules.email.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.infrastructure.persistence.entity.EmailMessageEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailMessageMapper {

    private final ObjectMapper objectMapper;

    public EmailMessage toDomain(EmailMessageEntity e) {
        return EmailMessage.reconstitute(
                EmailMessageId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                e.getEventId(),
                e.getExternalMessageId(),
                e.getExternalThreadId(),
                e.getFromAddress(),
                e.getFromName(),
                deserializeAddresses(e.getToAddressesJson()),
                e.getSubject(),
                e.getBodyText(),
                e.getBodyHtml(),
                e.getRawHeadersJson(),
                e.getClassification(),
                e.getClassificationConfidence(),
                e.getProcessingStatus(),
                e.getProcessingAttempts(),
                e.getLastAttemptAt(),
                e.getProcessedAt(),
                e.getReceivedAt(),
                e.getCreatedAt()
        );
    }

    public EmailMessageEntity toEntity(EmailMessage m) {
        return EmailMessageEntity.builder()
                .id(m.getId().value())
                .promoterId(m.getPromoterId().value())
                .eventId(m.getEventId())
                .externalMessageId(m.getExternalMessageId())
                .externalThreadId(m.getExternalThreadId())
                .fromAddress(m.getFromAddress())
                .fromName(m.getFromName())
                .toAddressesJson(serializeAddresses(m.getToAddresses()))
                .subject(m.getSubject())
                .bodyText(m.getBodyText())
                .bodyHtml(m.getBodyHtml())
                .rawHeadersJson(m.getRawHeadersJson())
                .classification(m.getClassification())
                .classificationConfidence(m.getClassificationConfidence())
                .processingStatus(m.getProcessingStatus())
                .processingAttempts(m.getProcessingAttempts())
                .lastAttemptAt(m.getLastAttemptAt())
                .processedAt(m.getProcessedAt())
                .receivedAt(m.getReceivedAt())
                .createdAt(m.getCreatedAt())
                .build();
    }

    // ── JSON helpers ──────────────────────────────────────────────────────────

    private String serializeAddresses(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(addresses);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<String> deserializeAddresses(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
