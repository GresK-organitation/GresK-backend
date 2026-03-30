package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.exception.InvalidGenreException;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.Description;
import com.gresk.modules.promoter.domain.valueobject.Location;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.modules.promoter.domain.valueobject.PromoterName;
import com.gresk.modules.promoter.port.PromoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdatePromoterProfileUseCase {

    private final PromoterRepository promoterRepository;

    public Mono<Void> execute (UpdatePromoterProfileCommand command){
        return Mono.defer(() -> {
            PromoterId id = PromoterId.of(command.promoterId());
            return promoterRepository.findById(id)
                    .switchIfEmpty(Mono.error(new PromoterNotFoundException(command.promoterId())))
                    .flatMap( promoter -> {

                        PromoterName newName = command.name() != null
                                ? new PromoterName(command.name())
                                : promoter.getName();

                        Description newDescription = command.description() != null
                                ? new Description(command.description())
                                : promoter.getDescription();

                        Location newLocation = command.city() != null
                                ? new Location(command.city(), command.country(), command.address())
                                : promoter.getLocation();

                        Set<MusicGenre> newGenres = command.musicalGenres() != null
                                ? parseGenres(command.musicalGenres())
                                : promoter.getMusicalGenres();

                        Promoter updated = Promoter.reconstitute(
                                promoter.getId(),
                                promoter.getEmail(),
                                promoter.getPassword(),
                                newName,
                                newDescription,
                                newLocation,
                                newGenres,
                                promoter.getStatus(),
                                promoter.getCreatedAt(),
                                promoter.isActive()
                        );

                        return promoterRepository.save(updated);

                    })
                    .then();
        });
    }

    private Set<MusicGenre> parseGenres (Set<String> raw){
        Set<MusicGenre> result = new LinkedHashSet<>();
        for (String s : raw){
            try {
                result.add(MusicGenre.valueOf(s));
            }catch (IllegalArgumentException e){
                throw new InvalidGenreException(s);
            }
        }
        return result;
    }
}
