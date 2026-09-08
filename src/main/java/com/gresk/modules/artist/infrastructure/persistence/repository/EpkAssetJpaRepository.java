package com.gresk.modules.artist.infrastructure.persistence.repository;

import com.gresk.modules.artist.domain.model.valueobject.EpkAssetType;
import com.gresk.modules.artist.infrastructure.persistence.entity.EpkAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EpkAssetJpaRepository extends JpaRepository<EpkAssetEntity, UUID> {
    Optional<EpkAssetEntity> findByIdAndPromoterId(UUID id, UUID promoterId);
    List<EpkAssetEntity> findByArtistId(UUID artistId);
    List<EpkAssetEntity> findByArtistIdAndType(UUID artistId, EpkAssetType type);
}
