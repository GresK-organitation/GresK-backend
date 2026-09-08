package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.port.in.ListRoomingListsByTourUseCase;
import com.gresk.modules.logistics.application.query.ListRoomingListsByTourQuery;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListRoomingListsByTourService implements ListRoomingListsByTourUseCase {

    private final RoomingListRepositoryPort roomingListRepository;
    private final TourRepositoryPort tourRepository;

    @Override
    public List<RoomingList> execute(ListRoomingListsByTourQuery query) {
        LogisticsLookups.requireTour(tourRepository, query.tourId(), query.promoterId());
        return roomingListRepository.findAllByTourId(TourId.of(query.tourId()));
    }
}
