package com.gresk.modules.logistics.application.dto;

import java.time.Instant;
import java.util.List;

public record TravelPartyResponse(String id, String tourId, List<MemberView> members, Instant updatedAt) {

    public record MemberView(String id, String personType, String personId, String displayName, String role,
                              List<NeedView> needs, RoomingPreferenceView roomingPreference, boolean active) {
    }

    public record NeedView(String type, String description) {
    }

    public record RoomingPreferenceView(String preferredRoomType, String preferredRoommateId, List<String> doNotShareWith) {
    }
}
