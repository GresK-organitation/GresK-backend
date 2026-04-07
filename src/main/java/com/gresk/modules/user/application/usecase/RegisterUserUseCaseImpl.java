package com.gresk.modules.user.application.usecase;

import com.gresk.modules.user.application.command.RegisterUserCommand;
import com.gresk.modules.user.domain.model.City;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.in.RegisterUserUseCase;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public UserId execute(RegisterUserCommand command) {
        Email email = Email.of(command.email());

        Set<MusicGenre> genres = command.musicGenres() != null
                ? command.musicGenres().stream().map(MusicGenre::valueOf).collect(Collectors.toSet())
                : Set.of();

        User user = User.create(
                email,
                Name.of(command.name()),
                Description.of(command.description()),
                City.of(command.city()),
                genres
        );

        return userRepository.save(user).getId();
    }
}
