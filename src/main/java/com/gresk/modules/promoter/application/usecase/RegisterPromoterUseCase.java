package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.application.port.in.RegisterPromoterPort;
import com.gresk.modules.promoter.domain.exception.EmailAlreadyExistsException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepositoryPort;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterPromoterUseCase implements RegisterPromoterPort {

    private final PromoterRepositoryPort promoterRepository;

    @Transactional
    @Override
    public PromoterId execute(RegisterPromoterCommand command) {
        Email email = new Email(command.email());

        if (promoterRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(command.email());
        }

        Name name = new Name(command.name());
        Address address = new Address(command.street(), City.of(command.city()), command.country());
        Description description = new Description(command.description());
        AssetId logoAssetId = AssetId.of(command.logoAssetId());

        Set<MusicGenre> genres = command.musicalGenres() != null
                ? command.musicalGenres().stream()
                        .map(MusicGenre::valueOf)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                : new LinkedHashSet<>();

        Promoter promoter = Promoter.create(
                PromoterId.of(command.promoterId().toString()),
                logoAssetId,
                email,
                name,
                address,
                description,
                genres,
                command.phone(),
                command.website()
        );

        return promoterRepository.save(promoter).getId();
    }
}
