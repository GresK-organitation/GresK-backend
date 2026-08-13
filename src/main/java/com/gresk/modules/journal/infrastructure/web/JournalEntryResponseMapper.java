package com.gresk.modules.journal.infrastructure.web;

import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalMedia;
import com.gresk.modules.journal.domain.model.JournalMediaType;
import com.gresk.modules.journal.domain.model.RatingCriterion;
import com.gresk.shared.domain.port.out.ImageUrlResolverPort;
import com.gresk.shared.domain.port.out.VideoUrlResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JournalEntryResponseMapper {

    private final ImageUrlResolverPort imageUrlResolver;
    private final VideoUrlResolverPort videoUrlResolver;

    public JournalEntryResponse toResponse(JournalEntry entry) {
        return new JournalEntryResponse(
                entry.getId().toString(),
                entry.getUserId().toString(),
                entry.getArtistNameFree(),
                entry.getArtistId() != null ? entry.getArtistId().toString() : null,
                entry.getDate().value().toString(),
                entry.getDate().precision().name(),
                entry.getVenueName(),
                entry.getCity(),
                entry.getEventId() != null ? entry.getEventId().toString() : null,
                entry.getNotes(),
                entry.getCriteria().stream().map(this::toResponse).toList(),
                entry.getMedia().stream().map(this::toResponse).toList(),
                entry.getGenre() != null ? entry.getGenre().name() : null,
                entry.getVisibility().name(),
                entry.getSource().name(),
                entry.getCreatedAt().toString(),
                entry.getUpdatedAt().toString()
        );
    }

    private RatingCriterionResponse toResponse(RatingCriterion criterion) {
        return new RatingCriterionResponse(criterion.label(), criterion.value());
    }

    private JournalMediaResponse toResponse(JournalMedia media) {
        String url = media.mediaType() == JournalMediaType.PHOTO
                ? imageUrlResolver.resolveOrDefault(media.assetId())
                : videoUrlResolver.resolveOrNull(media.assetId());

        return new JournalMediaResponse(
                media.id().toString(),
                media.mediaType().name(),
                url,
                media.displayOrder(),
                media.durationSeconds()
        );
    }
}
