package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "travel_party_members")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPartyMemberEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_party_id", nullable = false)
    private TravelPartyEntity travelParty;

    @Column(name = "person_type", nullable = false)
    private String personType;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "preferred_room_type")
    private String preferredRoomType;

    @Column(name = "preferred_roommate_id")
    private UUID preferredRoommateId;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "travel_party_member_do_not_share", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "other_member_id")
    private Set<UUID> doNotShareWith = new HashSet<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "travel_party_member_needs", joinColumns = @JoinColumn(name = "member_id"))
    private List<IndividualNeedEmbeddable> needs = new ArrayList<>();
}
