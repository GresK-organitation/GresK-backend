package com.gresk.modules.journal.infrastructure.web;

import java.util.List;

public record JournalEntryResponse(
        String  entryId,
        String  userId,
        String  artistName,
        String  artistId,
        String  date,
        String  datePrecision,
        String  venueName,
        String  city,
        String  eventId,
        String  notes,
        List<RatingCriterionResponse> criteria,
        List<JournalMediaResponse>    media,
        String  genre,
        String  visibility,
        String  source,
        String  createdAt,
        String  updatedAt
) {}
