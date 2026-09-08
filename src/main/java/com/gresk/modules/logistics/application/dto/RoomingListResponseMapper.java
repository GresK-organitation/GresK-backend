package com.gresk.modules.logistics.application.dto;

import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAllotment;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAssignment;
import org.springframework.stereotype.Component;

@Component
public class RoomingListResponseMapper {

    public RoomingListResponse toResponse(RoomingList roomingList) {
        return new RoomingListResponse(
                roomingList.getId().toString(),
                roomingList.getTourId().toString(),
                roomingList.getHotelName(),
                roomingList.getHotelAddress(),
                roomingList.getCheckInDate(),
                roomingList.getCheckOutDate(),
                roomingList.getAllotments().stream().map(this::toAllotmentView).toList(),
                roomingList.getAssignments().stream().map(this::toAssignmentView).toList(),
                roomingList.getUpdatedAt());
    }

    private RoomingListResponse.AllotmentView toAllotmentView(RoomAllotment a) {
        return new RoomingListResponse.AllotmentView(a.roomType().name(), a.quantity(),
                a.costPerNight().amount(), a.costPerNight().currency());
    }

    private RoomingListResponse.AssignmentView toAssignmentView(RoomAssignment a) {
        return new RoomingListResponse.AssignmentView(a.id().toString(), a.roomType().name(),
                a.occupantIds().stream().map(Object::toString).toList(), a.roomNumber());
    }
}
