package com.gresk.modules.logistics.application.dto;

import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.model.valueobject.EmergencyContact;
import com.gresk.modules.logistics.domain.model.valueobject.PointOfInterest;
import com.gresk.modules.logistics.domain.model.valueobject.TourLeg;
import org.springframework.stereotype.Component;

@Component
public class TourResponseMapper {

    public TourResponse toResponse(Tour tour) {
        return new TourResponse(
                tour.getId().toString(),
                tour.getPromoterId().toString(),
                tour.getArtistId().toString(),
                tour.getName(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getStatus().name(),
                tour.getLegs().stream().map(this::toLegView).toList(),
                tour.getEmergencyContacts().stream().map(this::toContactView).toList(),
                tour.getPointsOfInterest().stream().map(this::toPoiView).toList(),
                tour.getNotes(),
                tour.getCreatedAt(),
                tour.getUpdatedAt());
    }

    private TourResponse.LegView toLegView(TourLeg leg) {
        return new TourResponse.LegView(leg.bookingId().toString(), leg.sequenceOrder(), leg.showDate(),
                leg.venueName(), leg.venueCity());
    }

    private TourResponse.ContactView toContactView(EmergencyContact c) {
        return new TourResponse.ContactView(c.name(), c.role(), c.phone(), c.notes());
    }

    private TourResponse.PoiView toPoiView(PointOfInterest p) {
        Double lat = p.coordinates() == null ? null : p.coordinates().latitude();
        Double lon = p.coordinates() == null ? null : p.coordinates().longitude();
        return new TourResponse.PoiView(p.name(), p.category().name(), p.address(), lat, lon, p.notes());
    }
}
