package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.query.GetRoomingListQuery;
import com.gresk.modules.logistics.domain.model.RoomingList;

public interface GetRoomingListUseCase {
    RoomingList execute(GetRoomingListQuery query);
}
