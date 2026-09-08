package com.gresk.modules.logistics.domain.port.out;

import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.RoomingListId;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface RoomingListRepositoryPort {
    RoomingList save(RoomingList roomingList);
    Optional<RoomingList> findById(RoomingListId id);
    Optional<RoomingList> findByIdAndPromoterId(RoomingListId id, PromoterId promoterId);
    List<RoomingList> findAllByTourId(TourId tourId);
}
