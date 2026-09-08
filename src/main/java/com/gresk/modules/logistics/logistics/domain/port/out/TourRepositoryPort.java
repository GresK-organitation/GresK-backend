package com.gresk.modules.logistics.domain.port.out;

import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TourStatus;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface TourRepositoryPort {
    Tour save(Tour tour);
    Optional<Tour> findById(TourId id);
    Optional<Tour> findByIdAndPromoterId(TourId id, PromoterId promoterId);
    List<Tour> findByPromoterId(PromoterId promoterId, TourStatus status);
}
