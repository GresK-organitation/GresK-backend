package com.gresk.modules.journal.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.journal.domain.model.*;
import com.gresk.modules.journal.infrastructure.persistence.entity.JournalEntryEntity;
import com.gresk.modules.journal.infrastructure.persistence.entity.JournalEntryMediaEntity;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JournalEntryMapper {

    private final ObjectMapper objectMapper;

    public JournalEntry toDomain(JournalEntryEntity e) {
        List<JournalMedia> media = e.getMedia().stream()
                .map(this::toDomainMedia)
                .toList();

        return JournalEntry.reconstitute(
                JournalEntryId.of(e.getId()),
                UserId.of(e.getUserId()),
                e.getCreatedAt(),
                e.getSource(),
                e.getArtistName(),
                e.getArtistId() != null ? ArtistId.of(e.getArtistId()) : null,
                ApproxDate.reconstitute(e.getApproxDate(), e.getDatePrecision()),
                e.getVenueName(),
                e.getCity(),
                e.getEventId() != null ? new EventId(e.getEventId()) : null,
                e.getNotes(),
                deserializeCriteria(e.getRatingCriteriaJson()),
                media,
                e.getGenre(),
                e.getVisibility(),
                e.getUpdatedAt()
        );
    }

    public JournalEntryEntity toEntity(JournalEntry entry) {
        JournalEntryEntity entity = JournalEntryEntity.builder()
                .id(entry.getId().value())
                .userId(entry.getUserId().value())
                .artistName(entry.getArtistNameFree())
                .artistId(entry.getArtistId() != null ? entry.getArtistId().value() : null)
                .approxDate(entry.getDate().value())
                .datePrecision(entry.getDate().precision())
                .venueName(entry.getVenueName())
                .city(entry.getCity())
                .eventId(entry.getEventId() != null ? entry.getEventId().value() : null)
                .notes(entry.getNotes())
                .ratingCriteriaJson(serializeCriteria(entry.getCriteria()))
                .genre(entry.getGenre())
                .visibility(entry.getVisibility())
                .source(entry.getSource())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();

        List<JournalEntryMediaEntity> mediaEntities = new ArrayList<>();
        for (JournalMedia m : entry.getMedia()) {
            mediaEntities.add(toEntityMedia(m, entity));
        }
        entity.setMedia(mediaEntities);

        return entity;
    }

    // ── Media ─────────────────────────────────────────────────────────────────

    private JournalMedia toDomainMedia(JournalEntryMediaEntity e) {
        return JournalMedia.reconstitute(
                JournalMediaId.of(e.getId()),
                e.getMediaType(),
                AssetId.of(e.getAssetId()),
                e.getDisplayOrder(),
                e.getDurationSeconds()
        );
    }

    private JournalEntryMediaEntity toEntityMedia(JournalMedia media, JournalEntryEntity entry) {
        return JournalEntryMediaEntity.builder()
                .id(media.id().value())
                .entry(entry)
                .mediaType(media.mediaType())
                .assetId(media.assetId().value())
                .displayOrder(media.displayOrder())
                .durationSeconds(media.durationSeconds())
                .createdAt(java.time.Instant.now())
                .build();
    }

    // ── JSON helpers ──────────────────────────────────────────────────────────

    private String serializeCriteria(List<RatingCriterion> criteria) {
        if (criteria == null || criteria.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(criteria);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<RatingCriterion> deserializeCriteria(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<RatingCriterion>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
