package com.gresk.modules.logistics.application.command;

import java.util.List;

public record UpdateRoomAssignmentsCommand(String roomingListId, String promoterId,
                                            List<RoomAssignmentInput> assignments) {
}
