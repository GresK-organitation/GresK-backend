package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.query.ListRoomingListsByTourQuery;
import com.gresk.modules.logistics.domain.model.RoomingList;

import java.util.List;

public interface ListRoomingListsByTourUseCase {
    List<RoomingList> execute(ListRoomingListsByTourQuery query);
}
