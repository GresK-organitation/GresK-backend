package com.gresk.modules.booking.application.dto;

import com.gresk.modules.booking.application.query.GanttBar;
import com.gresk.modules.booking.application.query.TerritorialConflict;
import com.gresk.modules.booking.application.query.TimelineEntry;
import com.gresk.modules.booking.application.query.VenueGanttRow;
import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.valueobject.DaySheet;
import com.gresk.modules.booking.domain.model.valueobject.DaySheetEntry;
import com.gresk.modules.booking.domain.model.valueobject.Milestone;
import com.gresk.modules.booking.domain.model.valueobject.TerritorialExclusivity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingResponseMapper {

    public BookingResponse toResponse(Booking b) {
        TerritorialExclusivity ex = b.getExclusivity();
        return new BookingResponse(
                b.getId().toString(),
                b.getPromoterId().toString(),
                b.getArtistId().toString(),
                b.getVenue().venueId() == null ? null : b.getVenue().venueId().toString(),
                b.getVenue().venueName(),
                b.getVenue().territory().country(),
                b.getVenue().territory().region(),
                b.getVenue().territory().city(),
                b.getVenue().territory().radiusKm(),
                b.getEventDate(),
                b.getStatus().name(),
                b.getHoldExpiresAt(),
                b.getMilestones().stream().map(this::toResponse).toList(),
                toResponse(b.getDaySheet()),
                ex == null ? null : ex.protectedTerritory().country(),
                ex == null ? null : ex.protectedTerritory().region(),
                ex == null ? null : ex.protectedTerritory().city(),
                ex == null ? null : ex.protectedTerritory().radiusKm(),
                ex == null ? null : ex.daysBefore(),
                ex == null ? null : ex.daysAfter(),
                b.getLinkedEventId() == null ? null : b.getLinkedEventId().toString(),
                b.getLinkedContractId() == null ? null : b.getLinkedContractId().toString(),
                b.getNotes(),
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }

    public MilestoneResponse toResponse(Milestone m) {
        return new MilestoneResponse(m.milestoneId().toString(), m.type().name(), m.title(),
                m.offset().daysOffset(), m.dueDate(), m.status().name(), m.completedAt(), m.notes());
    }

    public DaySheetResponse toResponse(DaySheet daySheet) {
        if (daySheet == null) return null;
        List<DaySheetEntryResponse> entries = daySheet.entries().stream().map(this::toResponse).toList();
        return new DaySheetResponse(daySheet.showDate().toString(), entries);
    }

    public DaySheetEntryResponse toResponse(DaySheetEntry entry) {
        return new DaySheetEntryResponse(entry.time().toString(), entry.type().name(), entry.label(), entry.notes());
    }

    public VenueGanttRowResponse toResponse(VenueGanttRow row) {
        return new VenueGanttRowResponse(row.venueId(), row.venueName(),
                row.bars().stream().map(this::toResponse).toList());
    }

    public GanttBarResponse toResponse(GanttBar bar) {
        return new GanttBarResponse(bar.bookingId(), bar.artistName(), bar.start(), bar.end(), bar.status());
    }

    public TimelineEntryResponse toResponse(TimelineEntry entry) {
        return new TimelineEntryResponse(entry.bookingId(), entry.venueName(), entry.eventDate(),
                entry.status(), entry.pendingMilestones(), entry.nextMilestoneTitle());
    }

    public TerritorialConflictResponse toResponse(TerritorialConflict conflict) {
        return new TerritorialConflictResponse(conflict.conflictingBookingId(), conflict.venueName(), conflict.eventDate());
    }
}
