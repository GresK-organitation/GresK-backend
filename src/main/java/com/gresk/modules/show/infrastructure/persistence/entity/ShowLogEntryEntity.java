package com.gresk.modules.show.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "show_log_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowLogEntryEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "show_id", nullable = false)
    private UUID showId;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(length = 255)
    private String actor;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "related_party", length = 255)
    private String relatedParty;

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String attachmentsJson;
}
