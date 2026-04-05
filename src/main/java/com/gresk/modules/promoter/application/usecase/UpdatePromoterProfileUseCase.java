package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.application.port.in.UpdatePromoterProfilePort;
import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.exception.InvalidGenreException;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepository;
import com.gresk.modules.promoter.domain.valueobject.Description;
import com.gresk.modules.promoter.domain.valueobject.Location;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.modules.promoter.domain.valueobject.PromoterName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdatePromoterProfileUseCase implements UpdatePromoterProfilePort {

    private final PromoterRepository promoterRepository;

    @Transactional
    @Override
    public void execute(UpdatePromoterProfileCommand command) {
        PromoterId id = PromoterId.of(command.promoterId());
        Promoter promoter = promoterRepository.findById(id)
                .orElseThrow(() -> new PromoterNotFoundException(command.promoterId()));

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

        promoterRepository.save(updated);
    }

    private Set<MusicGenre> parseGenres(Set<String> raw) {
        Set<MusicGenre> result = new LinkedHashSet<>();
        for (String s : raw) {
            try {
                result.add(MusicGenre.valueOf(s));
            } catch (IllegalArgumentException e) {
                throw new InvalidGenreException(s);
            }
        }
        return result;
    }
}
