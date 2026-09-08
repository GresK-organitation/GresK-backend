package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "room_assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAssignmentEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rooming_list_id", nullable = false)
    private RoomingListEntity roomingList;

    @Column(name = "room_type", nullable = false)
    private String roomType;

    @Column(name = "room_number")
    private String roomNumber;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_assignment_occupants", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "occupant_id")
    @OrderColumn(name = "occupant_order")
    private List<UUID> occupantIds = new ArrayList<>();
}
