package com.gresk.modules.musicdna.infrastructure.persistence.repository;

import com.gresk.modules.musicdna.infrastructure.persistence.entity.UserMusicDnaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserMusicDnaJpaRepository extends JpaRepository<UserMusicDnaEntity, UUID> {
    Optional<UserMusicDnaEntity> findByUserId(UUID userId);
}
