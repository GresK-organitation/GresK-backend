package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.DeleteArtistPort;
import com.gresk.modules.artist.domain.exception.ArtistNotFoundException;
import com.gresk.modules.artist.domain.exception.ArtistNotOwnedByPromoterException;
import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteArtistUseCase implements DeleteArtistPort {

    private final ArtistRepositoryPort artistRepository;

    @Override
    @Transactional
    public void execute(String artistId, UUID promoterAccountId) {
        Artist artist = artistRepository.findById(ArtistId.of(artistId))
                .orElseThrow(() -> new ArtistNotFoundException(artistId));

        PromoterId promoterId = PromoterId.of(promoterAccountId.toString());
        if (!artist.belongsTo(promoterId)) {
            throw new ArtistNotOwnedByPromoterException(artistId);
        }

        artistRepository.deleteById(ArtistId.of(artistId));
    }
}
