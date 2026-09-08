package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.dto.ResolvedEpkDownload;
import com.gresk.modules.artist.application.port.in.ResolvePublicEpkDownloadPort;
import com.gresk.modules.artist.domain.exception.EpkAssetNotFoundException;
import com.gresk.modules.artist.domain.exception.EpkShareLinkNotFoundException;
import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.EpkShareLink;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetVersion;
import com.gresk.modules.artist.domain.port.out.EpkAssetRepositoryPort;
import com.gresk.modules.artist.domain.port.out.EpkShareLinkRepositoryPort;
import com.gresk.shared.domain.port.out.RawFileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ResolvePublicEpkDownloadUseCase implements ResolvePublicEpkDownloadPort {

    private final EpkShareLinkRepositoryPort shareLinkRepository;
    private final EpkAssetRepositoryPort     epkAssetRepository;
    private final RawFileStoragePort         rawFileStorage;

    @Override
    public ResolvedEpkDownload execute(String token) {
        // El token ES la autorización: sin promoterId, endpoint público.
        EpkShareLink link = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> new EpkShareLinkNotFoundException(token));

        link.registerDownload(); // valida revoked/expired/maxDownloads, lanza si no usable
        shareLinkRepository.save(link);

        EpkAsset asset = epkAssetRepository.findById(link.getEpkAssetId())
                .orElseThrow(() -> new EpkAssetNotFoundException(link.getEpkAssetId().toString()));
        EpkAssetVersion version = asset.findVersion(link.getVersionNumber());

        String url = rawFileStorage.resolveUrl(version.storedFile());
        return new ResolvedEpkDownload(url, version.fileName());
    }
}
