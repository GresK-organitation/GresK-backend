package com.gresk.modules.logistics.infrastructure.persistence.mapper;

import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.model.ItineraryId;
import com.gresk.modules.logistics.domain.model.SegmentType;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.valueobject.ItinerarySegment;
import com.gresk.modules.logistics.infrastructure.persistence.entity.ItineraryEntity;
import com.gresk.modules.logistics.infrastructure.persistence.entity.ItinerarySegmentEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class ItineraryMapper {

    public Itinerary toDomain(ItineraryEntity entity) {
        List<ItinerarySegment> segments = entity.getSegments().stream().map(this::toDomain).toList();
        return Itinerary.reconstitute(ItineraryId.of(entity.getId()), TourId.of(entity.getTourId()),
                PromoterId.of(entity.getPromoterId()), entity.getCreatedAt(), segments, entity.getUpdatedAt());
    }

    private ItinerarySegment toDomain(ItinerarySegmentEntity e) {
        return new ItinerarySegment(e.getId(), SegmentType.valueOf(e.getType()), e.getDepartureAt(),
                e.getDepartureLocation(), e.getArrivalAt(), e.getArrivalLocation(), e.getCarrierOrOperator(),
                e.getSegmentCode(), e.getConfirmationReference(), e.getSeatOrCapacityInfo(), e.getVoucherUrl(),
                e.getNotes(), e.getTravelerIds());
    }

    public ItineraryEntity toEntity(Itinerary itinerary) {
        ItineraryEntity entity = ItineraryEntity.builder()
                .id(itinerary.getId().value())
                .tourId(itinerary.getTourId().value())
                .promoterId(itinerary.getPromoterId().value())
                .createdAt(itinerary.getCreatedAt())
                .updatedAt(itinerary.getUpdatedAt())
                .build();

        List<ItinerarySegmentEntity> segments = new ArrayList<>();
        for (ItinerarySegment s : itinerary.getSegments()) {
            segments.add(toEntity(s, entity));
        }
        entity.setSegments(segments);
        return entity;
    }

    private ItinerarySegmentEntity toEntity(ItinerarySegment s, ItineraryEntity parent) {
        return ItinerarySegmentEntity.builder()
                .id(s.id())
                .itinerary(parent)
                .type(s.type().name())
                .departureAt(s.departureAt())
                .departureLocation(s.departureLocation())
                .arrivalAt(s.arrivalAt())
                .arrivalLocation(s.arrivalLocation())
                .carrierOrOperator(s.carrierOrOperator())
                .segmentCode(s.segmentCode())
                .confirmationReference(s.confirmationReference())
                .seatOrCapacityInfo(s.seatOrCapacityInfo())
                .voucherUrl(s.voucherUrl())
                .notes(s.notes())
                .travelerIds(new HashSet<>(s.travelerIds()))
                .build();
    }
}
