package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.UploadEpkAssetCommand;
import com.gresk.modules.artist.application.port.in.UploadEpkAssetPort;
import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetType;
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
public class UploadEpkAssetUseCase implements UploadEpkAssetPort {

    private final RawFileStoragePort     rawFileStorage;
    private final EpkAssetRepositoryPort epkAssetRepository;

    @Override
    public EpkAsset execute(UploadEpkAssetCommand command) {
        ArtistId artistId = ArtistId.of(command.artistId());
        AssetId storedFile = rawFileStorage.upload(command.file(), "artists/epk/" + artistId.value());

        EpkAsset asset = EpkAsset.create(
                artistId,
                PromoterId.of(command.promoterId()),
                EpkAssetType.valueOf(command.type()),
                command.label(),
                storedFile,
                command.file().getOriginalFilename(),
                command.file().getContentType(),
                command.file().getSize(),
                command.uploadedByUserId()
        );
        return epkAssetRepository.save(asset);
    }
}
