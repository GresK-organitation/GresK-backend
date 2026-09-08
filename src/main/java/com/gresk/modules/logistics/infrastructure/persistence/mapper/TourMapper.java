package com.gresk.modules.logistics.infrastructure.persistence.mapper;

import com.gresk.modules.logistics.domain.model.POICategory;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TourStatus;
import com.gresk.modules.logistics.domain.model.valueobject.EmergencyContact;
import com.gresk.modules.logistics.domain.model.valueobject.PointOfInterest;
import com.gresk.modules.logistics.domain.model.valueobject.TourLeg;
import com.gresk.modules.logistics.infrastructure.persistence.entity.EmergencyContactEmbeddable;
import com.gresk.modules.logistics.infrastructure.persistence.entity.PointOfInterestEmbeddable;
import com.gresk.modules.logistics.infrastructure.persistence.entity.TourEntity;
import com.gresk.modules.logistics.infrastructure.persistence.entity.TourLegEmbeddable;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Coordinates;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TourMapper {

    public Tour toDomain(TourEntity entity) {
        List<TourLeg> legs = entity.getLegs().stream().map(this::toDomain).toList();
        List<EmergencyContact> contacts = entity.getEmergencyContacts().stream().map(this::toDomain).toList();
        List<PointOfInterest> pois = entity.getPointsOfInterest().stream().map(this::toDomain).toList();

        return Tour.reconstitute(TourId.of(entity.getId()), PromoterId.of(entity.getPromoterId()), entity.getArtistId(),
                entity.getCreatedAt(), entity.getName(), entity.getStartDate(), entity.getEndDate(),
                TourStatus.valueOf(entity.getStatus()), legs, contacts, pois, entity.getNotes(), entity.getUpdatedAt());
    }

    private TourLeg toDomain(TourLegEmbeddable e) {
        return new TourLeg(e.getBookingId(), e.getSequenceOrder(), e.getShowDate(), e.getVenueName(), e.getVenueCity());
    }

    private EmergencyContact toDomain(EmergencyContactEmbeddable e) {
        return new EmergencyContact(e.getName(), e.getRole(), e.getPhone(), e.getNotes());
    }

    private PointOfInterest toDomain(PointOfInterestEmbeddable e) {
        Coordinates coordinates = (e.getLatitude() != null && e.getLongitude() != null)
                ? Coordinates.of(e.getLatitude(), e.getLongitude()) : null;
        return new PointOfInterest(e.getName(), POICategory.valueOf(e.getCategory()), e.getAddress(), coordinates, e.getNotes());
    }

    public TourEntity toEntity(Tour tour) {
        List<TourLegEmbeddable> legs = new ArrayList<>();
        for (TourLeg leg : tour.getLegs()) {
            legs.add(TourLegEmbeddable.builder().bookingId(leg.bookingId()).sequenceOrder(leg.sequenceOrder())
                    .showDate(leg.showDate()).venueName(leg.venueName()).venueCity(leg.venueCity()).build());
        }
        List<EmergencyContactEmbeddable> contacts = new ArrayList<>();
        for (EmergencyContact c : tour.getEmergencyContacts()) {
            contacts.add(EmergencyContactEmbeddable.builder().name(c.name()).role(c.role()).phone(c.phone())
                    .notes(c.notes()).build());
        }
        List<PointOfInterestEmbeddable> pois = new ArrayList<>();
        for (PointOfInterest p : tour.getPointsOfInterest()) {
            Double lat = p.coordinates() == null ? null : p.coordinates().latitude();
            Double lon = p.coordinates() == null ? null : p.coordinates().longitude();
            pois.add(PointOfInterestEmbeddable.builder().name(p.name()).category(p.category().name())
                    .address(p.address()).latitude(lat).longitude(lon).notes(p.notes()).build());
        }

        return TourEntity.builder()
                .id(tour.getId().value())
                .promoterId(tour.getPromoterId().value())
                .artistId(tour.getArtistId())
                .name(tour.getName())
                .startDate(tour.getStartDate())
                .endDate(tour.getEndDate())
                .status(tour.getStatus().name())
                .notes(tour.getNotes())
                .createdAt(tour.getCreatedAt())
                .updatedAt(tour.getUpdatedAt())
                .legs(legs)
                .emergencyContacts(contacts)
                .pointsOfInterest(pois)
                .build();
    }
}
