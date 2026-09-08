package com.gresk.modules.rider.infrastructure.persistence.entity;

import com.gresk.modules.rider.domain.model.RiderItemCategory;
import com.gresk.modules.rider.domain.model.valueobject.FulfillmentSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hospitality_rider_line_items")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HospitalityRiderLineItemEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)
    private HospitalityRiderEntity rider;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20, nullable = false)
    private RiderItemCategory category;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "attributes", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String attributesJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_source", length = 20, nullable = false)
    private FulfillmentSource fulfillmentSource;

    @Column(name = "equiv_requested_spec", length = 255)
    private String equivRequestedSpec;

    @Column(name = "equiv_proposed_alternative", length = 255)
    private String equivProposedAlternative;

    @Column(name = "equiv_status", length = 20)
    private String equivStatus;

    @Column(name = "equiv_proposed_by", length = 20)
    private String equivProposedBy;

    @Column(name = "equiv_notes", columnDefinition = "TEXT")
    private String equivNotes;

    @Column(name = "equiv_proposed_at")
    private Instant equivProposedAt;

    @Column(name = "equiv_decided_at")
    private Instant equivDecidedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
