package com.gresk.modules.artist.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetVersion;
import com.gresk.modules.artist.infrastructure.persistence.entity.EpkAssetEntity;
import com.gresk.modules.artist.infrastructure.persistence.entity.EpkAssetVersionEmbeddable;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.AssetId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EpkAssetMapper {

    public EpkAsset toDomain(EpkAssetEntity entity) {
        List<EpkAssetVersion> versions = entity.getVersions().stream()
                .map(this::toDomainVersion)
                .toList();

        return EpkAsset.reconstitute(
                EpkAssetId.of(entity.getId()),
                ArtistId.of(entity.getArtistId()),
                PromoterId.of(entity.getPromoterId()),
                entity.getType(),
                entity.getLabel(),
                versions,
                entity.isArchived(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public EpkAssetEntity toEntity(EpkAsset asset) {
        return EpkAssetEntity.builder()
                .id(asset.getId().value())
                .artistId(asset.getArtistId().value())
                .promoterId(asset.getPromoterId().value())
                .type(asset.getType())
                .label(asset.getLabel())
                .archived(asset.isArchived())
                .versions(toEmbeddableVersions(asset.getVersions()))
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }

    public List<EpkAssetVersionEmbeddable> toEmbeddableVersions(List<EpkAssetVersion> versions) {
        List<EpkAssetVersionEmbeddable> result = new ArrayList<>();
        for (EpkAssetVersion v : versions) {
            result.add(EpkAssetVersionEmbeddable.builder()
                    .versionNumber(v.versionNumber())
                    .storedFile(v.storedFile().value())
                    .fileName(v.fileName())
                    .mimeType(v.mimeType())
                    .fileSizeBytes(v.fileSizeBytes())
                    .uploadedByUserId(v.uploadedByUserId())
                    .uploadedAt(v.uploadedAt())
                    .build());
        }
        return result;
    }

    private EpkAssetVersion toDomainVersion(EpkAssetVersionEmbeddable e) {
        return new EpkAssetVersion(
                e.getVersionNumber(),
                AssetId.reconstitute(e.getStoredFile()),
                e.getFileName(),
                e.getMimeType(),
                e.getFileSizeBytes(),
                e.getUploadedByUserId(),
                e.getUploadedAt()
        );
    }
}
