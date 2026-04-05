package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.application.port.in.RegisterPromoterPort;
import com.gresk.modules.promoter.application.port.out.PasswordHasher;
import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.exception.EmailAlreadyExistsException;
import com.gresk.modules.promoter.domain.exception.InvalidGenreException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepository;
import com.gresk.modules.promoter.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterPromoterUseCase implements RegisterPromoterPort {

    private final PromoterRepository promoterRepository;
    private final PasswordHasher passwordHasher;

    @Transactional
    @Override
    public PromoterId execute(RegisterPromoterCommand command) {
        Email email = new Email(command.email());

        if (promoterRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(command.email());
        }

        String hashedPassword = passwordHasher.hash(command.rawPassword());
        PromoterName name = new PromoterName(command.name());
        Password password = new Password(hashedPassword);
        Location location = new Location(command.city(), command.country(), command.address());
        Description description = new Description(command.description());

        Promoter promoter = Promoter.create(email, password, name, location, description);

        if (command.musicalGenres() != null) {
            command.musicalGenres().forEach(raw -> {
                try {
                    promoter.addGenre(MusicGenre.valueOf(raw));
                } catch (IllegalArgumentException e) {
                    throw new InvalidGenreException(raw);
                }
            });
        }

        return promoterRepository.save(promoter).getId();
    }
}
