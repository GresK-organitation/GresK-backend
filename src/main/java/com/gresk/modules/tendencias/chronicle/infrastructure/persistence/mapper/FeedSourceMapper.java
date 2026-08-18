package com.gresk.modules.tendencias.chronicle.infrastructure.persistence.mapper;

import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceId;
import com.gresk.modules.tendencias.chronicle.infrastructure.persistence.entity.FeedSourceEntity;
import org.springframework.stereotype.Component;

@Component
public class FeedSourceMapper {

    public FeedSourceEntity toEntity(FeedSource feedSource) {
        return FeedSourceEntity.builder()
                .id(feedSource.getId().value())
                .name(feedSource.getName())
                .feedUrl(feedSource.getFeedUrl())
                .sourceUrl(feedSource.getSourceUrl())
                .status(feedSource.getStatus())
                .requestedAt(feedSource.getRequestedAt())
                .reviewedBy(feedSource.getReviewedBy())
                .reviewedAt(feedSource.getReviewedAt())
                .lastFetchedAt(feedSource.getLastFetchedAt())
                .build();
    }

    public FeedSource toDomain(FeedSourceEntity entity) {
        return FeedSource.reconstitute(
                FeedSourceId.of(entity.getId()),
                entity.getName(),
                entity.getFeedUrl(),
                entity.getSourceUrl(),
                entity.getRequestedAt(),
                entity.getStatus(),
                entity.getReviewedBy(),
                entity.getReviewedAt(),
                entity.getLastFetchedAt()
        );
    }
}
