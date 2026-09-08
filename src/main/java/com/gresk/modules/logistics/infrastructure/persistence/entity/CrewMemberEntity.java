package com.gresk.modules.logistics.infrastructure.persistence.entity;

import com.gresk.modules.artist.infrastructure.persistence.entity.IdentityDocumentEmbeddable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

/** Reutiliza artist.IdentityDocumentEmbeddable: mismo shape que artist.BandMember, sin duplicarlo. */
@Entity
@Table(name = "crew_members")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewMemberEntity {

    @Id
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "default_role", nullable = false, length = 100)
    private String defaultRole;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "crew_member_document", joinColumns = @JoinColumn(name = "crew_member_id"))
    private List<IdentityDocumentEmbeddable> documents = new ArrayList<>();
}
