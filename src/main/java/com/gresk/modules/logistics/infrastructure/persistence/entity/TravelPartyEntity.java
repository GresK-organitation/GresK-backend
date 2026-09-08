package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "travel_parties")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPartyEntity {

    @Id
    private UUID id;

    @Column(name = "tour_id", nullable = false, unique = true)
    private UUID tourId;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "travelParty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelPartyMemberEntity> members = new ArrayList<>();
}
