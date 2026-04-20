package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.GetArtistByIdPort;
import com.gresk.modules.artist.domain.exception.ArtistNotFoundException;
import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetArtistByIdUseCase implements GetArtistByIdPort {

    private final ArtistRepositoryPort artistRepository;

    @Override
    public Artist execute(String artistId, String promoterId) {
        return artistRepository
                .findByIdAndPromoterId(ArtistId.of(artistId), PromoterId.of(promoterId))
                .orElseThrow(() -> new ArtistNotFoundException(artistId));
    }
}
