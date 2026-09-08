package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.UpdateRoomAssignmentsCommand;
import com.gresk.modules.logistics.domain.model.RoomingList;

public interface UpdateRoomAssignmentsUseCase {
    RoomingList execute(UpdateRoomAssignmentsCommand command);
}
