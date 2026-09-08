package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.ListEpkAssetsPort;
import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.EpkAssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListEpkAssetsUseCase implements ListEpkAssetsPort {

    private final EpkAssetRepositoryPort epkAssetRepository;

    @Override
    public List<EpkAsset> execute(String artistId, String promoterId) {
        return epkAssetRepository.findAllByArtistId(ArtistId.of(artistId));
    }
}
