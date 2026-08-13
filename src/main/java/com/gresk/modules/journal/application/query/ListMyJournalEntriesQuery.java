package com.gresk.modules.journal.application.query;

import com.gresk.shared.domain.MusicGenre;

import java.time.LocalDate;

public record ListMyJournalEntriesQuery(
        String     userId,
        String     artistId,   // nullable
        MusicGenre genre,      // nullable
        LocalDate  dateFrom,   // nullable
        LocalDate  dateTo,     // nullable
        int        page,
        int        size
) {}
