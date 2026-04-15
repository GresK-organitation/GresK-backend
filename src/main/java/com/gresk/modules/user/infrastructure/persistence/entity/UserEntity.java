package com.gresk.modules.user.infrastructure.persistence.entity;

import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.user.domain.model.UserTier;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false,  nullable = false)
    private UUID id;

    @Column(name = "account_id", nullable = false, unique = true)
    private UUID accountId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(length = 600)
    private String description;

    @Column(name = "avatar_asset_id", length = 255)
    private String avatarAssetId;

    @Column(nullable = false, length = 100)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserTier tier;

    @Column(nullable = false)
    private Integer loyaltyPoints;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_genres", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", length = 50)
    @Builder.Default
    private Set<MusicGenre> musicGenres = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.loyaltyPoints == null) this.loyaltyPoints = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateProfile(String name, String description, String city, Set<MusicGenre> musicGenres) {
        this.name = name;
        this.description = description;
        this.city = city;
        this.musicGenres = (musicGenres != null) ? new HashSet<>(musicGenres) : new HashSet<>();
    }

    public void updateAvatar(String avatarAssetId) {
        this.avatarAssetId = avatarAssetId;
    }

    public void updateTier(UserTier tier) {
        this.tier = tier;
    }

    public void updateLoyaltyPoints(int points) {
        this.loyaltyPoints = points;
    }
}
