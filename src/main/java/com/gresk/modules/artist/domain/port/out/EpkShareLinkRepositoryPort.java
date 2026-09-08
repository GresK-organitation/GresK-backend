package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.EpkShareLink;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.model.valueobject.EpkShareLinkId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EpkShareLinkRepositoryPort {
    EpkShareLink save(EpkShareLink link);
    Optional<EpkShareLink> findById(EpkShareLinkId id);
    Optional<EpkShareLink> findByToken(String token);
    List<EpkShareLink> findAllByEpkAssetId(EpkAssetId epkAssetId);
    void deleteExpiredBefore(Instant cutoff);
}
