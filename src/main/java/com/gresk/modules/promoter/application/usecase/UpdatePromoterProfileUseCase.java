package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.application.port.in.UpdatePromoterProfilePort;
import com.gresk.modules.promoter.domain.exception.PromoterNotActiveException;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepositoryPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.port.out.AccountStatusPort;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdatePromoterProfileUseCase implements UpdatePromoterProfilePort {

    private final PromoterRepositoryPort promoterRepository;
    private final AccountStatusPort accountStatusPort;

    @Transactional
    @Override
    public void execute(UpdatePromoterProfileCommand command) {
        UUID accountId = UUID.fromString(command.promoterId());
        if (accountStatusPort.getStatus(accountId) != AccountStatus.ACTIVE) {
            throw new PromoterNotActiveException("Promoter account is not active: " + accountId);
        }

        PromoterId id = PromoterId.of(command.promoterId());
        Promoter promoter = promoterRepository.findById(id)
                .orElseThrow(() -> new PromoterNotFoundException(command.promoterId()));

        Name name = new Name(command.name());
        Address address = new Address(command.street(), City.of(command.city()), command.country());
        Description description = new Description(command.description());

        promoter.updateBasicInfo(name, address, description);

        if (command.musicalGenres() != null) {
            Set<MusicGenre> genres = command.musicalGenres().stream()
                    .map(MusicGenre::valueOf)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            promoter.replaceGenres(genres);
        }

        promoterRepository.save(promoter);
    }
}
