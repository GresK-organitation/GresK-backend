package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.CreateRoomingListCommand;
import com.gresk.modules.logistics.domain.model.RoomingList;

public interface CreateRoomingListUseCase {
    RoomingList execute(CreateRoomingListCommand command);
}
