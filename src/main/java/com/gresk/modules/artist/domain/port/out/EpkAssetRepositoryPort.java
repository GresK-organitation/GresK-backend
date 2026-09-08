package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetType;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface EpkAssetRepositoryPort {
    EpkAsset save(EpkAsset asset);
    Optional<EpkAsset> findById(EpkAssetId id);
    Optional<EpkAsset> findByIdAndPromoterId(EpkAssetId id, PromoterId promoterId);
    List<EpkAsset> findAllByArtistId(ArtistId artistId);
    List<EpkAsset> findAllByArtistIdAndType(ArtistId artistId, EpkAssetType type);
    void deleteById(EpkAssetId id);
}
