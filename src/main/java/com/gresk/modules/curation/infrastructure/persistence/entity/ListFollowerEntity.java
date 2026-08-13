package com.gresk.modules.curation.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "list_followers")
@IdClass(ListFollowerId.class)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ListFollowerEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private CuratedListEntity list;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "followed_at", nullable = false, updatable = false)
    private Instant followedAt;
}
