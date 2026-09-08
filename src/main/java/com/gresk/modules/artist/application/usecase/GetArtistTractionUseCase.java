package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.GetArtistTractionPort;
import com.gresk.modules.artist.domain.exception.ArtistTractionNotFoundException;
import com.gresk.modules.artist.domain.model.ArtistTractionSnapshot;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistTractionSnapshotRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetArtistTractionUseCase implements GetArtistTractionPort {

    private final ArtistTractionSnapshotRepositoryPort snapshotRepository;

    @Override
    public ArtistTractionSnapshot execute(String artistId) {
        return snapshotRepository.findLatestByArtistId(ArtistId.of(artistId))
                .orElseThrow(() -> new ArtistTractionNotFoundException(artistId));
    }
}
