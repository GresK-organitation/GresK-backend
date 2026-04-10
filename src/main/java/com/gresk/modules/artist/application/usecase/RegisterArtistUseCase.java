package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.RegisterArtistCommand;
import com.gresk.modules.artist.application.dto.ArtistResponse;
import com.gresk.modules.artist.application.port.in.RegisterArtistPort;
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
public class RegisterArtistUseCase implements RegisterArtistPort {

    private final ArtistRepositoryPort artistRepository;

    @Override
    @Transactional
    public ArtistResponse execute(RegisterArtistCommand command) {
        Artist artist = Artist.create(
                ArtistId.generate(),
                PromoterId.of(command.promoterId()),
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
