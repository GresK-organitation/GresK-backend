package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.UpdateArtistCommand;
import com.gresk.modules.artist.application.dto.ArtistResponse;
import com.gresk.modules.artist.application.port.in.UpdateArtistPort;
import com.gresk.modules.artist.domain.exception.ArtistNotFoundException;
import com.gresk.modules.artist.domain.exception.ArtistNotOwnedByPromoterException;
import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.ArtistId;
import com.gresk.modules.artist.domain.model.ArtistStatus;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateArtistUseCase implements UpdateArtistPort {

    private final ArtistRepositoryPort artistRepository;

    @Override
    @Transactional
    public ArtistResponse execute(UpdateArtistCommand command) {
        Artist artist = artistRepository.findById(ArtistId.of(command.artistId()))
                .orElseThrow(() -> new ArtistNotFoundException(command.artistId()));

        PromoterId promoterId = PromoterId.of(command.promoterId());
        if (!artist.belongsTo(promoterId)) {
            throw new ArtistNotOwnedByPromoterException(command.artistId());
        }

        artist.update(
                command.name(),
                command.origin(),
                command.genres(),
                command.imageUrl(),
                command.bio(),
                ArtistStatus.valueOf(command.status()),
                command.fee(),
                command.followers(),
                command.contact(),
                command.socialSpotify(),
                command.socialInstagram(),
                command.tags()
        );

        return ArtistResponse.from(artistRepository.save(artist));
    }
}
