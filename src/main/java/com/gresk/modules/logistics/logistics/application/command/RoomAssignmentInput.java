package com.gresk.modules.logistics.application.command;

import java.util.List;

public record RoomAssignmentInput(String id, String roomType, List<String> occupantIds, String roomNumber) {
}
