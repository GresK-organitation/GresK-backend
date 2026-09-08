package com.gresk.modules.booking.infrastructure.persistence.mapper;

import com.gresk.modules.booking.domain.model.Booking;
import com.gresk.modules.booking.domain.model.BookingId;
import com.gresk.modules.booking.domain.model.BookingStatus;
import com.gresk.modules.booking.domain.model.DaySheetItemType;
import com.gresk.modules.booking.domain.model.MilestoneStatus;
import com.gresk.modules.booking.domain.model.MilestoneType;
import com.gresk.modules.booking.domain.model.valueobject.DaySheet;
import com.gresk.modules.booking.domain.model.valueobject.DaySheetEntry;
import com.gresk.modules.booking.domain.model.valueobject.Milestone;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneOffset;
import com.gresk.modules.booking.domain.model.valueobject.Territory;
import com.gresk.modules.booking.domain.model.valueobject.TerritorialExclusivity;
import com.gresk.modules.booking.domain.model.valueobject.VenueRef;
import com.gresk.modules.booking.infrastructure.persistence.entity.BookingDaySheetEntryEntity;
import com.gresk.modules.booking.infrastructure.persistence.entity.BookingEntity;
import com.gresk.modules.booking.infrastructure.persistence.entity.BookingMilestoneEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class BookingMapper {

    public Booking toDomain(BookingEntity entity) {
        VenueRef venue = new VenueRef(entity.getVenueId(), entity.getVenueName(),
                new Territory(entity.getVenueCountry(), entity.getVenueRegion(), entity.getVenueCity(), entity.getVenueRadiusKm()));

        TerritorialExclusivity exclusivity = entity.getExclusivityCountry() == null ? null
                : new TerritorialExclusivity(
                        new Territory(entity.getExclusivityCountry(), entity.getExclusivityRegion(),
                                entity.getExclusivityCity(), entity.getExclusivityRadiusKm()),
                        entity.getExclusivityDaysBefore(), entity.getExclusivityDaysAfter());

        List<Milestone> milestones = entity.getMilestones().stream()
                .map(this::toDomain)
                .toList();

        DaySheet daySheet = entity.getDaySheetShowDate() == null ? null
                : new DaySheet(entity.getDaySheetShowDate(),
                        entity.getDaySheetEntries().stream()
                                .sorted(Comparator.comparingInt(BookingDaySheetEntryEntity::getSortOrder))
                                .map(this::toDomain)
                                .toList());

        return Booking.reconstitute(
                BookingId.of(entity.getId()),
                PromoterId.of(entity.getPromoterId()),
                entity.getArtistId(),
                entity.getCreatedAt(),
                venue,
                entity.getEventDate(),
                BookingStatus.valueOf(entity.getStatus()),
                entity.getHoldExpiresAt(),
                milestones,
                daySheet,
                exclusivity,
                entity.getLinkedEventId(),
                entity.getLinkedContractId(),
                entity.getNotes(),
                entity.getUpdatedAt()
        );
    }

    private Milestone toDomain(BookingMilestoneEntity e) {
        return new Milestone(e.getId(), MilestoneType.valueOf(e.getType()), e.getTitle(),
                new MilestoneOffset(e.getOffsetDays()), e.getDueDate(), MilestoneStatus.valueOf(e.getStatus()),
                e.getCompletedAt(), e.getNotes());
    }

    private DaySheetEntry toDomain(BookingDaySheetEntryEntity e) {
        return new DaySheetEntry(e.getEntryTime(), DaySheetItemType.valueOf(e.getType()), e.getLabel(), e.getNotes());
    }

    public BookingEntity toEntity(Booking booking) {
        VenueRef venue = booking.getVenue();
        TerritorialExclusivity exclusivity = booking.getExclusivity();
        DaySheet daySheet = booking.getDaySheet();

        BookingEntity entity = BookingEntity.builder()
                .id(booking.getId().value())
                .promoterId(booking.getPromoterId().value())
                .artistId(booking.getArtistId())
                .venueId(venue.venueId())
                .venueName(venue.venueName())
                .venueCountry(venue.territory().country())
                .venueRegion(venue.territory().region())
                .venueCity(venue.territory().city())
                .venueRadiusKm(venue.territory().radiusKm())
                .eventDate(booking.getEventDate())
                .status(booking.getStatus().name())
                .holdExpiresAt(booking.getHoldExpiresAt())
                .exclusivityCountry(exclusivity == null ? null : exclusivity.protectedTerritory().country())
                .exclusivityRegion(exclusivity == null ? null : exclusivity.protectedTerritory().region())
                .exclusivityCity(exclusivity == null ? null : exclusivity.protectedTerritory().city())
                .exclusivityRadiusKm(exclusivity == null ? null : exclusivity.protectedTerritory().radiusKm())
                .exclusivityDaysBefore(exclusivity == null ? null : exclusivity.daysBefore())
                .exclusivityDaysAfter(exclusivity == null ? null : exclusivity.daysAfter())
                .daySheetShowDate(daySheet == null ? null : daySheet.showDate())
                .linkedEventId(booking.getLinkedEventId())
                .linkedContractId(booking.getLinkedContractId())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();

        List<BookingMilestoneEntity> milestoneEntities = new ArrayList<>();
        for (Milestone m : booking.getMilestones()) {
            milestoneEntities.add(BookingMilestoneEntity.builder()
                    .id(m.milestoneId())
                    .booking(entity)
                    .type(m.type().name())
                    .title(m.title())
                    .offsetDays(m.offset().daysOffset())
                    .dueDate(m.dueDate())
                    .status(m.status().name())
                    .completedAt(m.completedAt())
                    .notes(m.notes())
                    .build());
        }
        entity.setMilestones(milestoneEntities);

        List<BookingDaySheetEntryEntity> dayEntries = new ArrayList<>();
        if (daySheet != null) {
            int order = 0;
            for (DaySheetEntry e : daySheet.entries()) {
                dayEntries.add(BookingDaySheetEntryEntity.builder()
                        .id(java.util.UUID.randomUUID())
                        .booking(entity)
                        .entryTime(e.time())
                        .type(e.type().name())
                        .label(e.label())
                        .notes(e.notes())
                        .sortOrder(order++)
                        .build());
            }
        }
        entity.setDaySheetEntries(dayEntries);

        return entity;
    }
}
