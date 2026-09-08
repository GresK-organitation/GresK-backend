package com.gresk.modules.logistics.application.command;

import java.util.List;

public record RoomingPreferenceInput(String preferredRoomType, String preferredRoommateId, List<String> doNotShareWith) {
}
