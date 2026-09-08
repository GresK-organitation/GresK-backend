package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.GenerateEpkShareLinkCommand;
import com.gresk.modules.artist.application.port.in.GenerateEpkShareLinkPort;
import com.gresk.modules.artist.domain.exception.EpkAssetNotFoundException;
import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.EpkShareLink;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.port.out.EpkAssetRepositoryPort;
import com.gresk.modules.artist.domain.port.out.EpkShareLinkRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerateEpkShareLinkUseCase implements GenerateEpkShareLinkPort {

    private final EpkAssetRepositoryPort     epkAssetRepository;
    private final EpkShareLinkRepositoryPort shareLinkRepository;

    @Override
    public EpkShareLink execute(GenerateEpkShareLinkCommand command) {
        PromoterId promoterId = PromoterId.of(command.promoterId());
        EpkAssetId epkAssetId = EpkAssetId.of(command.epkAssetId());

        EpkAsset asset = epkAssetRepository.findByIdAndPromoterId(epkAssetId, promoterId)
                .orElseThrow(() -> new EpkAssetNotFoundException(command.epkAssetId()));

        int versionNumber = command.versionNumber() != null
                ? command.versionNumber()
                : asset.currentVersion().versionNumber();

        EpkShareLink link = EpkShareLink.issue(
                epkAssetId, versionNumber, promoterId,
                Duration.ofHours(command.expiresInHours()),
                command.maxDownloads(),
                command.createdByUserId()
        );
        return shareLinkRepository.save(link);
    }
}
