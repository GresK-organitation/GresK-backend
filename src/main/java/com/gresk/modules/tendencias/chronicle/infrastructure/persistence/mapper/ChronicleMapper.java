package com.gresk.modules.tendencias.chronicle.infrastructure.persistence.mapper;

import com.gresk.modules.tendencias.chronicle.domain.model.*;
import com.gresk.modules.tendencias.chronicle.infrastructure.persistence.entity.ChronicleEntity;
import org.springframework.stereotype.Component;

@Component
public class ChronicleMapper {

    public ChronicleEntity toEntity(Chronicle chronicle) {
        return ChronicleEntity.builder()
                .id(chronicle.getId().value())
                .feedSourceId(chronicle.getFeedSourceId().value())
                .title(chronicle.getTitle())
                .excerpt(chronicle.getExcerpt().value())
                .link(chronicle.getLink())
                .guid(chronicle.getGuid())
                .sourceName(chronicle.getSource().sourceName())
                .sourceUrl(chronicle.getSource().sourceUrl())
                .originalPublishedAt(chronicle.getOriginalPublishedAt())
                .ingestedAt(chronicle.getIngestedAt())
                .status(chronicle.getStatus())
                .build();
    }

    public Chronicle toDomain(ChronicleEntity entity) {
        return Chronicle.reconstitute(
                ChronicleId.of(entity.getId()),
                entity.getTitle(),
                new ChronicleExcerpt(entity.getExcerpt()),
                entity.getLink(),
                entity.getGuid(),
                new SourceAttribution(entity.getSourceName(), entity.getSourceUrl()),
                FeedSourceId.of(entity.getFeedSourceId()),
                entity.getOriginalPublishedAt(),
                entity.getIngestedAt(),
                entity.getStatus()
        );
    }
}
