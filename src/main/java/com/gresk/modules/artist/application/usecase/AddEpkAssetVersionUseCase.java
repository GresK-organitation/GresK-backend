package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.AddEpkAssetVersionCommand;
import com.gresk.modules.artist.application.port.in.AddEpkAssetVersionPort;
import com.gresk.modules.artist.domain.exception.EpkAssetNotFoundException;
import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.port.out.EpkAssetRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.port.out.RawFileStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddEpkAssetVersionUseCase implements AddEpkAssetVersionPort {

    private final EpkAssetRepositoryPort epkAssetRepository;
    private final RawFileStoragePort     rawFileStorage;

    @Override
    public EpkAsset execute(AddEpkAssetVersionCommand command) {
        PromoterId promoterId = PromoterId.of(command.promoterId());
        EpkAssetId epkAssetId = EpkAssetId.of(command.epkAssetId());

        EpkAsset asset = epkAssetRepository.findByIdAndPromoterId(epkAssetId, promoterId)
                .orElseThrow(() -> new EpkAssetNotFoundException(command.epkAssetId()));

        AssetId storedFile = rawFileStorage.upload(command.file(), "artists/epk/" + asset.getArtistId().value());
        asset.addVersion(storedFile, command.file().getOriginalFilename(),
                command.file().getContentType(), command.file().getSize(), command.uploadedByUserId());

        return epkAssetRepository.save(asset);
    }
}
