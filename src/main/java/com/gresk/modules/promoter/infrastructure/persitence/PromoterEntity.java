package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.PromoterStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "promoters")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoterEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 600)
    private String description;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromoterStatus status;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "promoter_genres",
            joinColumns = @JoinColumn(name = "promoter_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", length = 50)
    @Builder.Default
    private Set<MusicGenre> genres = new LinkedHashSet<>();
}
