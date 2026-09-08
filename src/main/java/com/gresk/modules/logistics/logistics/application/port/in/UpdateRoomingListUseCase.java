package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.UpdateRoomingListCommand;
import com.gresk.modules.logistics.domain.model.RoomingList;

public interface UpdateRoomingListUseCase {
    RoomingList execute(UpdateRoomingListCommand command);
}
