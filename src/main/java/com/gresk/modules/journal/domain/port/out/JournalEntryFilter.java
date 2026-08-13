package com.gresk.modules.journal.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.MusicGenre;

import java.time.LocalDate;
import java.util.Optional;

public record JournalEntryFilter(
        UserId               userId,
        Optional<ArtistId>   artistId,
        Optional<MusicGenre> genre,
        Optional<LocalDate>  dateFrom,
        Optional<LocalDate>  dateTo
) {
    public static JournalEntryFilter forUser(UserId userId) {
        return new JournalEntryFilter(userId, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }
}
