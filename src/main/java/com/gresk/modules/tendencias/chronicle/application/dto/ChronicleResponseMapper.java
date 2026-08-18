package com.gresk.modules.tendencias.chronicle.application.dto;

import com.gresk.modules.tendencias.chronicle.domain.model.Chronicle;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSource;

public final class ChronicleResponseMapper {

    private ChronicleResponseMapper() {
    }

    public static ChronicleResponse toResponse(Chronicle chronicle) {
        return new ChronicleResponse(
                chronicle.getId().value(),
                chronicle.getTitle(),
                chronicle.getExcerpt().value(),
                chronicle.getLink(),
                chronicle.getSource().sourceName(),
                chronicle.getSource().sourceUrl(),
                chronicle.getOriginalPublishedAt(),
                chronicle.getIngestedAt()
        );
    }

    public static FeedSourceResponse toResponse(FeedSource feedSource) {
        return new FeedSourceResponse(
                feedSource.getId().value(),
                feedSource.getName(),
                feedSource.getFeedUrl(),
                feedSource.getSourceUrl(),
                feedSource.getStatus().name(),
                feedSource.getRequestedAt(),
                feedSource.getReviewedAt(),
                feedSource.getLastFetchedAt()
        );
    }
}
