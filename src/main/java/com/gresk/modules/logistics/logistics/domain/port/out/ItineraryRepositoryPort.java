package com.gresk.modules.logistics.domain.port.out;

import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.model.ItineraryId;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.Optional;

public interface ItineraryRepositoryPort {
    Itinerary save(Itinerary itinerary);
    Optional<Itinerary> findById(ItineraryId id);
    Optional<Itinerary> findByIdAndPromoterId(ItineraryId id, PromoterId promoterId);
    Optional<Itinerary> findByTourId(TourId tourId);
}
