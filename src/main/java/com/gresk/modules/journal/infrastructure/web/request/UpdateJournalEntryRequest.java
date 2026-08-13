package com.gresk.modules.journal.infrastructure.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record UpdateJournalEntryRequest(
        @Size(max = 150, message = "artistName must not exceed 150 characters")
        String artistName,   // nullable if artistId present

        String artistId,     // nullable, catalog link

        @NotNull(message = "date is required")
        LocalDate date,

        @NotNull(message = "datePrecision is required")
        @Pattern(regexp = "EXACT_DATE|MONTH|YEAR", message = "datePrecision must be EXACT_DATE, MONTH or YEAR")
        String datePrecision,

        @Size(max = 150, message = "venueName must not exceed 150 characters")
        String venueName,    // nullable

        @Size(max = 100, message = "city must not exceed 100 characters")
        String city,         // nullable

        String eventId,      // nullable, catalog link

        @Size(max = 1000, message = "notes must not exceed 1000 characters")
        String notes,        // nullable

        @Valid
        List<RatingCriterionRequest> criteria,

        String genre,        // nullable, MusicGenre name

        @NotNull(message = "visibility is required")
        @Pattern(regexp = "PRIVATE|PUBLIC", message = "visibility must be PRIVATE or PUBLIC")
        String visibility
) {}
