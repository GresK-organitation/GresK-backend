package com.gresk.modules.booking.application.usecase;

import com.gresk.modules.booking.application.command.DaySheetEntryInput;
import com.gresk.modules.booking.application.command.MilestoneBlueprintInput;
import com.gresk.modules.booking.application.command.TerritorialExclusivityInput;
import com.gresk.modules.booking.application.command.VenueRefInput;
import com.gresk.modules.booking.domain.model.DaySheetItemType;
import com.gresk.modules.booking.domain.model.MilestoneType;
import com.gresk.modules.booking.domain.model.valueobject.DaySheetEntry;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneBlueprint;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneOffset;
import com.gresk.modules.booking.domain.model.valueobject.Territory;
import com.gresk.modules.booking.domain.model.valueobject.TerritorialExclusivity;
import com.gresk.modules.booking.domain.model.valueobject.VenueRef;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/** Mapeo manual entre los records de entrada de la capa de aplicación y los VOs de dominio. */
final class BookingInputMapper {

    private BookingInputMapper() {
    }

    static VenueRef toVenueRef(VenueRefInput input) {
        UUID venueId = input.venueId() == null || input.venueId().isBlank() ? null : UUID.fromString(input.venueId());
        Territory territory = new Territory(input.country(), input.region(), input.city(), input.radiusKm());
        return new VenueRef(venueId, input.venueName(), territory);
    }

    static TerritorialExclusivity toExclusivity(TerritorialExclusivityInput input) {
        if (input == null) return null;
        Territory territory = new Territory(input.country(), input.region(), input.city(), input.radiusKm());
        return new TerritorialExclusivity(territory, input.daysBefore(), input.daysAfter());
    }

    static List<MilestoneBlueprint> toBlueprints(List<MilestoneBlueprintInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> new MilestoneBlueprint(MilestoneType.valueOf(i.type()), i.title(),
                        new MilestoneOffset(i.offsetDays())))
                .toList();
    }

    static List<DaySheetEntry> toDaySheetEntries(List<DaySheetEntryInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> new DaySheetEntry(LocalTime.parse(i.time()), DaySheetItemType.valueOf(i.type()),
                        i.label(), i.notes()))
                .toList();
    }
}
