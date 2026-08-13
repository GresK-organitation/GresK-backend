package com.gresk.modules.journal.domain.model;

import com.gresk.modules.journal.domain.exception.InvalidJournalEntryException;
import com.gresk.shared.domain.valueobject.AssetId;

import java.util.Objects;

public record JournalMedia(
        JournalMediaId id,
        JournalMediaType mediaType,
        AssetId assetId,
        int displayOrder,
        Integer durationSeconds
) {

    private static final int MAX_VIDEO_DURATION_SECONDS = 15;

    public JournalMedia {
        Objects.requireNonNull(id, "JournalMediaId is required");
        Objects.requireNonNull(mediaType, "JournalMediaType is required");
        Objects.requireNonNull(assetId, "AssetId is required");
        if (assetId.isEmpty()) {
            throw new InvalidJournalEntryException("Media assetId must not be blank");
        }
        if (mediaType == JournalMediaType.VIDEO) {
            if (durationSeconds == null || durationSeconds <= 0) {
                throw new InvalidJournalEntryException("Video clips must report a positive duration");
            }
            if (durationSeconds > MAX_VIDEO_DURATION_SECONDS) {
                throw new InvalidJournalEntryException(
                        "Video clips must be at most " + MAX_VIDEO_DURATION_SECONDS + " seconds");
            }
        } else if (durationSeconds != null) {
            throw new InvalidJournalEntryException("Photos must not have a duration");
        }
    }

    public static JournalMedia photo(AssetId assetId, int displayOrder) {
        return new JournalMedia(JournalMediaId.generate(), JournalMediaType.PHOTO, assetId, displayOrder, null);
    }

    public static JournalMedia video(AssetId assetId, int displayOrder, int durationSeconds) {
        return new JournalMedia(JournalMediaId.generate(), JournalMediaType.VIDEO, assetId, displayOrder, durationSeconds);
    }

    public static JournalMedia reconstitute(JournalMediaId id, JournalMediaType mediaType,
                                             AssetId assetId, int displayOrder, Integer durationSeconds) {
        return new JournalMedia(id, mediaType, assetId, displayOrder, durationSeconds);
    }
}
