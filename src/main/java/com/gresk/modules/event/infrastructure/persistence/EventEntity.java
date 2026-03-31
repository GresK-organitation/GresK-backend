package com.gresk.modules.event.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Table("events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    private String title;

    @Column("promoter_id")
    private UUID promoterId;

    private String status;
    private String genre;
    private BigDecimal amount;
    private String currency;

    @Column("total_capacity")
    private Integer totalCapacity;

    @Column("available_capacity")
    private Integer availableCapacity;

    @Column("event_date")
    private OffsetDateTime eventDate;

    private String city;
    private String address;
    private String venue;

    @Column("reveal_at")
    private OffsetDateTime revealAt;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;

    @Transient
    private boolean isNew;

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
