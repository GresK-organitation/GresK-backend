package com.gresk.modules.booking.domain.model.valueobject;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Cronograma detallado ("run sheet") del día de show de un {@code Booking}.
 */
public record DaySheet(LocalDate showDate, List<DaySheetEntry> entries) {

    public DaySheet {
        if (showDate == null) {
            throw new IllegalArgumentException("DaySheet showDate must not be null");
        }
        entries = entries == null
                ? List.of()
                : entries.stream().sorted(Comparator.comparing(DaySheetEntry::time)).toList();
    }

    public static DaySheet of(LocalDate showDate, List<DaySheetEntry> entries) {
        return new DaySheet(showDate, entries);
    }

    /** Usado cuando {@code Booking.reschedule(...)} mueve la fecha del evento. */
    public DaySheet retarget(LocalDate newShowDate) {
        return new DaySheet(newShowDate, entries);
    }

    public DaySheet withEntries(List<DaySheetEntry> newEntries) {
        return new DaySheet(showDate, newEntries);
    }
}
