package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooming_lists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomingListEntity {

    @Id
    private UUID id;

    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "hotel_name", nullable = false)
    private String hotelName;

    @Column(name = "hotel_address")
    private String hotelAddress;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rooming_list_allotments", joinColumns = @JoinColumn(name = "rooming_list_id"))
    private List<RoomAllotmentEmbeddable> allotments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "roomingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomAssignmentEntity> assignments = new ArrayList<>();
}
