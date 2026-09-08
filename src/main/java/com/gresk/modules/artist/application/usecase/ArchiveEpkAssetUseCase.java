package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.ArchiveEpkAssetPort;
import com.gresk.modules.artist.domain.exception.EpkAssetNotFoundException;
import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.port.out.EpkAssetRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArchiveEpkAssetUseCase implements ArchiveEpkAssetPort {

    private final EpkAssetRepositoryPort epkAssetRepository;

    @Override
    public void execute(String epkAssetId, String promoterId) {
        EpkAsset asset = epkAssetRepository.findByIdAndPromoterId(
                        EpkAssetId.of(epkAssetId), PromoterId.of(promoterId))
                .orElseThrow(() -> new EpkAssetNotFoundException(epkAssetId));
        asset.archive();
        epkAssetRepository.save(asset);
    }
}
