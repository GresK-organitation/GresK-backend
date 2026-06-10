package com.gresk.modules.email.infrastructure.persistence.entity;

import com.gresk.modules.email.domain.model.RiderVersionSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_rider_versions",
       uniqueConstraints = @UniqueConstraint(name = "uq_rider_event_version",
                                             columnNames = {"event_id", "version_number"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRiderVersionEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(name = "source_email_id")
    private UUID sourceEmailId;

    @Column(name = "rider_data", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String riderDataJson;

    @Column(name = "diff_from_prev", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String diffFromPrevJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by", nullable = false, length = 20)
    private RiderVersionSource createdBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
