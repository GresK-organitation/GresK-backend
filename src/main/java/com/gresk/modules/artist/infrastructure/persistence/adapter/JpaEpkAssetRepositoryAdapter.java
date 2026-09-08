package com.gresk.modules.artist.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetType;
import com.gresk.modules.artist.domain.port.out.EpkAssetRepositoryPort;
import com.gresk.modules.artist.infrastructure.persistence.mapper.EpkAssetMapper;
import com.gresk.modules.artist.infrastructure.persistence.repository.EpkAssetJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEpkAssetRepositoryAdapter implements EpkAssetRepositoryPort {

    private final EpkAssetJpaRepository jpaRepository;
    private final EpkAssetMapper        mapper;

    @Override
    @Transactional
    public EpkAsset save(EpkAsset asset) {
        // Igual que JpaArtistRepositoryAdapter: en updates hay que cargar la entidad
        // gestionada y mutarla in-place — mapper.toEntity() resetea version=null y
        // fuerza un persist() en vez de merge(), lanzando EntityExistsException.
        return jpaRepository.findById(asset.getId().value())
                .map(entity -> {
                    entity.updateLabel(asset.getLabel());
                    entity.updateArchived(asset.isArchived());
                    entity.replaceVersions(mapper.toEmbeddableVersions(asset.getVersions()));
                    return mapper.toDomain(jpaRepository.save(entity));
                })
                .orElseGet(() -> mapper.toDomain(jpaRepository.save(mapper.toEntity(asset))));
    }

    @Override
    public Optional<EpkAsset> findById(EpkAssetId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<EpkAsset> findByIdAndPromoterId(EpkAssetId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value()).map(mapper::toDomain);
    }

    @Override
    public List<EpkAsset> findAllByArtistId(ArtistId artistId) {
        return jpaRepository.findByArtistId(artistId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<EpkAsset> findAllByArtistIdAndType(ArtistId artistId, EpkAssetType type) {
        return jpaRepository.findByArtistIdAndType(artistId.value(), type).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(EpkAssetId id) {
        jpaRepository.deleteById(id.value());
    }
}
