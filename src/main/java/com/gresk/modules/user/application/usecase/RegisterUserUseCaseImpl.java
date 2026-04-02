package com.gresk.modules.user.application.usecase;

import com.gresk.modules.identity.domain.service.UserEmailUniquenessChecker;
import com.gresk.modules.user.application.command.RegisterUserCommand;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.in.RegisterUserUseCase;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;
import com.gresk.shared.domain.valueobject.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final UserEmailUniquenessChecker emailUniquenessChecker;

    @Override
    @Transactional
    public UserId execute(RegisterUserCommand command) {
        Email email = Email.of(command.email());
        emailUniquenessChecker.check(email);

        List<MusicGenre> genres = command.musicGenres().stream()
                .map(MusicGenre::valueOf)
                .toList();

        User user = User.create(
                email,
                Password.of(command.password()),
                Name.of(command.name()),
                Description.of(command.description()),
                genres
        );

        return userRepository.save(user).getId();
    }
}