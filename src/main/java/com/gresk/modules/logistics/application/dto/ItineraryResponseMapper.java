package com.gresk.modules.logistics.application.dto;

import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.model.valueobject.ItinerarySegment;
import org.springframework.stereotype.Component;

@Component
public class ItineraryResponseMapper {

    public ItineraryResponse toResponse(Itinerary itinerary) {
        return new ItineraryResponse(
                itinerary.getId().toString(),
                itinerary.getTourId().toString(),
                itinerary.getSegments().stream().map(this::toSegmentView).toList(),
                itinerary.getUpdatedAt());
    }

    private ItineraryResponse.SegmentView toSegmentView(ItinerarySegment s) {
        return new ItineraryResponse.SegmentView(
                s.id().toString(),
                s.type().name(),
                s.departureAt(),
                s.departureLocation(),
                s.arrivalAt(),
                s.arrivalLocation(),
                s.carrierOrOperator(),
                s.segmentCode(),
                s.confirmationReference(),
                s.seatOrCapacityInfo(),
                s.voucherUrl(),
                s.notes(),
                s.travelerIds().stream().map(Object::toString).toList());
    }
}
