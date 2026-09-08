package com.gresk.modules.booking.domain.model.valueobject;

import com.gresk.modules.booking.domain.model.DaySheetItemType;

import java.time.LocalTime;

/**
 * Línea individual del cronograma del día de show (load-in, soundcheck, puertas, set time...).
 */
public record DaySheetEntry(LocalTime time, DaySheetItemType type, String label, String notes) {

    public DaySheetEntry {
        if (time == null) {
            throw new IllegalArgumentException("DaySheetEntry time must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("DaySheetEntry type must not be null");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("DaySheetEntry label must not be blank");
        }
    }
}
