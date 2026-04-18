package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.CreateArtistCommand;
import com.gresk.modules.artist.application.port.in.CreateArtistPort;
import com.gresk.modules.artist.domain.exception.ArtistAlreadyExistsException;
import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistContact;
import com.gresk.modules.artist.domain.model.valueobject.ArtistFee;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.ArtistStatus;
import com.gresk.modules.artist.domain.model.valueobject.FollowerCount;
import com.gresk.modules.artist.domain.model.valueobject.SocialLinks;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.ImageUrl;
import com.gresk.shared.domain.valueobject.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateArtistUseCase implements CreateArtistPort {

    private final ArtistRepositoryPort artistRepository;

    @Override
    @Transactional
    public ArtistId execute(CreateArtistCommand command) {

        PromoterId promoterId = PromoterId.of(command.promoterId());

        if (artistRepository.existsByContactAndPromoterId(command.contact(), promoterId)) {
            throw new ArtistAlreadyExistsException(command.contact());
        }

        Set<MusicGenre> genres = command.genres() != null
                ? command.genres().stream()
                         .map(MusicGenre::valueOf)
                         .collect(Collectors.toCollection(LinkedHashSet::new))
                : new LinkedHashSet<>();

        ImageUrl imageUrl = (command.imageUrl() != null && !command.imageUrl().isBlank())
                ? ImageUrl.of(command.imageUrl())
                : new ImageUrl("");

        Set<String> tags = command.tags() != null
                ? new LinkedHashSet<>(command.tags())
                : new LinkedHashSet<>();

        Artist artist = Artist.create(
                promoterId,
                Name.of(command.name()),
                City.of(command.origin()),
                genres,
                imageUrl,
                Description.of(command.bio()),
                ArtistStatus.valueOf(command.status()),
                ArtistFee.of(command.fee()),
                FollowerCount.of(command.followers()),
                tags,
                ArtistContact.of(command.contact()),
                SocialLinks.of(command.instagramUrl(), command.spotifyUrl())
        );

        return artistRepository.save(artist).getId();
    }
}
