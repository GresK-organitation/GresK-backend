package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.EpkShareLink;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetVersion;
import com.gresk.shared.domain.port.out.RawFileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EpkResponseMapper {

    private final RawFileStoragePort rawFileStorage;

    public EpkAssetResponse toResponse(EpkAsset asset) {
        return new EpkAssetResponse(
                asset.getId().value().toString(), asset.getArtistId().value().toString(),
                asset.getPromoterId().value().toString(), asset.getType().name(), asset.getLabel(),
                asset.isArchived(), asset.getVersions().stream().map(this::toVersionResponse).toList(),
                asset.getCreatedAt(), asset.getUpdatedAt());
    }

    private EpkAssetVersionResponse toVersionResponse(EpkAssetVersion v) {
        return new EpkAssetVersionResponse(v.versionNumber(), rawFileStorage.resolveUrl(v.storedFile()),
                v.fileName(), v.mimeType(), v.fileSizeBytes(), v.uploadedByUserId(), v.uploadedAt());
    }

    public EpkShareLinkResponse toShareLinkResponse(EpkShareLink link) {
        return new EpkShareLinkResponse(
                link.getId().value().toString(), link.getEpkAssetId().value().toString(),
                link.getVersionNumber(), link.getToken(), "/api/v1/public/epk/" + link.getToken(),
                link.getExpiresAt(), link.getMaxDownloads(), link.getDownloadCount(),
                link.isRevoked(), link.getCreatedAt());
    }
}
