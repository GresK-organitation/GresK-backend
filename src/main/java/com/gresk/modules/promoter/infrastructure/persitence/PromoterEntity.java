package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.MusicGenre;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "promoters")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PromoterEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 600)
    private String description;

    @Column(name = "logo_asset_id", length = 255)
    private String logoAssetId;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(length = 255)
    private String street;

    @Column(length = 50)
    private String phone;

    @Column(length = 255)
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "promoter_genres", joinColumns = @JoinColumn(name = "promoter_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", length = 50)
    @Builder.Default
    private Set<MusicGenre> genres = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void updateProfile(String name, String description,
                               String city, String country, String street,
                               String phone, String website,
                               Set<MusicGenre> genres) {
        this.name = name;
        this.description = description;
        this.city = city;
        this.country = country;
        this.street = street;
        this.phone = phone;
        this.website = website;
        this.genres = new HashSet<>(genres);
    }

    public void updateLogo(String logoAssetId) {
        this.logoAssetId = logoAssetId;
    }

    public void updateStatus(AccountStatus status) {
        this.status = status;
    }
}