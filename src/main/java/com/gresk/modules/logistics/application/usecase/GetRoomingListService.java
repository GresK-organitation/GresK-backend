package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.port.in.GetRoomingListUseCase;
import com.gresk.modules.logistics.application.query.GetRoomingListQuery;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetRoomingListService implements GetRoomingListUseCase {

    private final RoomingListRepositoryPort roomingListRepository;

    @Override
    public RoomingList execute(GetRoomingListQuery query) {
        return LogisticsLookups.requireRoomingListById(roomingListRepository, query.roomingListId(), query.promoterId());
    }
}
