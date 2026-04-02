package com.gresk.modules.user.application.usecase;

import com.gresk.modules.user.application.command.UpdateUserCommand;
import com.gresk.modules.user.domain.exception.UserNotFoundException;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.in.UpdateUserUseCase;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public User execute(UpdateUserCommand command) {
        UserId userId = UserId.from(command.userId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<MusicGenre> genres = command.musicGenres().stream()
                .map(MusicGenre::valueOf)
                .toList();

        user.updateProfile(
                Name.of(command.name()),
                Description.of(command.description()),
                genres
        );

        return userRepository.save(user);
    }
}