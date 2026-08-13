package com.gresk.modules.journal.application.command;

import com.gresk.modules.journal.domain.model.JournalEntrySource;
import com.gresk.modules.journal.domain.model.JournalVisibility;
import com.gresk.shared.domain.MusicGenre;

import java.time.LocalDate;
import java.util.List;

public record CreateJournalEntryCommand(
        String              userId,
        String              artistName,     // nullable if artistId present
        String              artistId,       // nullable, catalog link
        LocalDate           date,
        String              datePrecision,  // EXACT_DATE | MONTH | YEAR
        String              venueName,      // nullable
        String              city,           // nullable
        String              eventId,        // nullable, catalog link
        String              notes,          // nullable
        List<RatingCriterionInput> criteria,
        MusicGenre          genre,          // nullable
        JournalVisibility   visibility,
        JournalEntrySource  source
) {}
