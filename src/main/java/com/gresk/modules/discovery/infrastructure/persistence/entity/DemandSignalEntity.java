package com.gresk.modules.discovery.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "artist_demand_signals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandSignalEntity {

    @Id
    private UUID id;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
