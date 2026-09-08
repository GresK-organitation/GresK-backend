package com.gresk.modules.logistics.infrastructure.persistence.mapper;

import com.gresk.modules.logistics.domain.model.RoomType;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.RoomingListId;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAllotment;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAssignment;
import com.gresk.modules.logistics.infrastructure.persistence.entity.RoomAllotmentEmbeddable;
import com.gresk.modules.logistics.infrastructure.persistence.entity.RoomAssignmentEntity;
import com.gresk.modules.logistics.infrastructure.persistence.entity.RoomingListEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoomingListMapper {

    public RoomingList toDomain(RoomingListEntity entity) {
        List<RoomAllotment> allotments = entity.getAllotments().stream().map(this::toDomain).toList();
        List<RoomAssignment> assignments = entity.getAssignments().stream().map(this::toDomain).toList();
        return RoomingList.reconstitute(RoomingListId.of(entity.getId()), TourId.of(entity.getTourId()),
                PromoterId.of(entity.getPromoterId()), entity.getCreatedAt(), entity.getHotelName(),
                entity.getHotelAddress(), entity.getCheckInDate(), entity.getCheckOutDate(), allotments, assignments,
                entity.getUpdatedAt());
    }

    private RoomAllotment toDomain(RoomAllotmentEmbeddable e) {
        return new RoomAllotment(RoomType.valueOf(e.getRoomType()), e.getQuantity(), Money.of(e.getCostAmount(), e.getCurrency()));
    }

    private RoomAssignment toDomain(RoomAssignmentEntity e) {
        return new RoomAssignment(e.getId(), RoomType.valueOf(e.getRoomType()), List.copyOf(e.getOccupantIds()), e.getRoomNumber());
    }

    public RoomingListEntity toEntity(RoomingList roomingList) {
        List<RoomAllotmentEmbeddable> allotments = new ArrayList<>();
        for (RoomAllotment a : roomingList.getAllotments()) {
            allotments.add(RoomAllotmentEmbeddable.builder().roomType(a.roomType().name()).quantity(a.quantity())
                    .costAmount(a.costPerNight().amount()).currency(a.costPerNight().currency()).build());
        }

        RoomingListEntity entity = RoomingListEntity.builder()
                .id(roomingList.getId().value())
                .tourId(roomingList.getTourId().value())
                .promoterId(roomingList.getPromoterId().value())
                .hotelName(roomingList.getHotelName())
                .hotelAddress(roomingList.getHotelAddress())
                .checkInDate(roomingList.getCheckInDate())
                .checkOutDate(roomingList.getCheckOutDate())
                .createdAt(roomingList.getCreatedAt())
                .updatedAt(roomingList.getUpdatedAt())
                .allotments(allotments)
                .build();

        List<RoomAssignmentEntity> assignments = new ArrayList<>();
        for (RoomAssignment a : roomingList.getAssignments()) {
            assignments.add(RoomAssignmentEntity.builder().id(a.id()).roomingList(entity).roomType(a.roomType().name())
                    .roomNumber(a.roomNumber()).occupantIds(new ArrayList<>(a.occupantIds())).build());
        }
        entity.setAssignments(assignments);
        return entity;
    }
}
