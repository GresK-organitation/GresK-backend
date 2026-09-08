package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.GenerateRoomAssignmentsCommand;
import com.gresk.modules.logistics.domain.model.RoomingList;

public interface GenerateRoomAssignmentsUseCase {
    RoomingList execute(GenerateRoomAssignmentsCommand command);
}
