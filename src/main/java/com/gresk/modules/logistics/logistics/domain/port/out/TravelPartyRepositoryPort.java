package com.gresk.modules.logistics.domain.port.out;

import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.model.TravelPartyId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.Optional;

public interface TravelPartyRepositoryPort {
    TravelParty save(TravelParty travelParty);
    Optional<TravelParty> findById(TravelPartyId id);
    Optional<TravelParty> findByIdAndPromoterId(TravelPartyId id, PromoterId promoterId);
    Optional<TravelParty> findByTourId(TourId tourId);
}
