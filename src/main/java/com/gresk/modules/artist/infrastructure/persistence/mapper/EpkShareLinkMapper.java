package com.gresk.modules.artist.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.EpkShareLink;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.model.valueobject.EpkShareLinkId;
import com.gresk.modules.artist.infrastructure.persistence.entity.EpkShareLinkEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

@Component
public class EpkShareLinkMapper {

    public EpkShareLink toDomain(EpkShareLinkEntity e) {
        return EpkShareLink.reconstitute(
                EpkShareLinkId.of(e.getId()),
                EpkAssetId.of(e.getEpkAssetId()),
                e.getVersionNumber(),
                PromoterId.of(e.getPromoterId()),
                e.getToken(),
                e.getExpiresAt(),
                e.getMaxDownloads(),
                e.getDownloadCount(),
                e.isRevoked(),
                e.getCreatedByUserId(),
                e.getCreatedAt()
        );
    }

    public EpkShareLinkEntity toEntity(EpkShareLink link) {
        return EpkShareLinkEntity.builder()
                .id(link.getId().value())
                .epkAssetId(link.getEpkAssetId().value())
                .versionNumber(link.getVersionNumber())
                .promoterId(link.getPromoterId().value())
                .token(link.getToken())
                .expiresAt(link.getExpiresAt())
                .maxDownloads(link.getMaxDownloads())
                .downloadCount(link.getDownloadCount())
                .revoked(link.isRevoked())
                .createdByUserId(link.getCreatedByUserId())
                .createdAt(link.getCreatedAt())
                .build();
    }
}
