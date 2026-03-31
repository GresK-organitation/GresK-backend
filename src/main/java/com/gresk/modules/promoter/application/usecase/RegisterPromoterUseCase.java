package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.domain.exception.EmailAlreadyExistsException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.*;
import com.gresk.modules.promoter.port.PromoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class RegisterPromoterUseCase {

    private final PromoterRepository promoterRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<PromoterId> execute (RegisterPromoterCommand command){
        return Mono.defer(() -> {
            Email email = new Email(command.email());
            return promoterRepository.existsByEmail(email)
                    .flatMap(exists -> {
                        if (exists) {
                            return Mono.error(new EmailAlreadyExistsException(command.email()));
                        }
                        return Mono.fromCallable(() -> passwordEncoder.encode(command.rawPassword()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(hashedPassword -> {
                                    PromoterName name = new PromoterName(command.name());
                                    Password password = new Password(hashedPassword);
                                    Location location = new Location(command.city(), command.country(), command.address());
                                    Description description = new Description(command.description());

                                    Promoter promoter = Promoter.create(email, password, name, location, description);

                                    if (command.musicalGenres() != null) {
                                        command.musicalGenres().stream()
                                                .map(MusicGenre::valueOf)
                                                .forEach(promoter::addGenre);
                                    }

                                    return promoterRepository.save(promoter)
                                            .map(Promoter::getId);
                                });
                    });
        });
    }
}
