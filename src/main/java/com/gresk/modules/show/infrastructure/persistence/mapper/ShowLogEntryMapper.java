package com.gresk.modules.show.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.show.domain.model.ShowId;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.model.ShowLogEntryId;
import com.gresk.modules.show.domain.model.valueobject.LogEntryType;
import com.gresk.modules.show.infrastructure.persistence.entity.ShowLogEntryEntity;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShowLogEntryMapper {

    private final ObjectMapper objectMapper;

    public ShowLogEntry toDomain(ShowLogEntryEntity e) {
        return ShowLogEntry.reconstitute(
                new ShowLogEntryId(e.getId()), new ShowId(e.getShowId()),
                LogEntryType.valueOf(e.getType()), e.getActor(), e.getOccurredAt(),
                e.getDescription(), e.getRelatedParty(), deserializeAttachments(e.getAttachmentsJson())
        );
    }

    public ShowLogEntryEntity toEntity(ShowLogEntry entry) {
        return ShowLogEntryEntity.builder()
                .id(entry.getId().value())
                .showId(entry.getShowId().value())
                .type(entry.getType().name())
                .actor(entry.getActor())
                .occurredAt(entry.getOccurredAt())
                .description(entry.getDescription())
                .relatedParty(entry.getRelatedParty())
                .attachmentsJson(serialize(entry.getAttachments()))
                .build();
    }

    private String serialize(List<AssetId> attachments) {
        try {
            return objectMapper.writeValueAsString(attachments.stream().map(AssetId::value).toList());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<AssetId> deserializeAttachments(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            List<String> raw = objectMapper.readValue(json, new TypeReference<>() {});
            return raw.stream().map(AssetId::of).toList();
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
