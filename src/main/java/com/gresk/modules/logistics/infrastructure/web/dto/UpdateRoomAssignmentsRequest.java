package com.gresk.modules.logistics.infrastructure.web.dto;

import com.gresk.modules.logistics.application.command.RoomAssignmentInput;

import java.util.List;

public record UpdateRoomAssignmentsRequest(List<RoomAssignmentInput> assignments) {
}
