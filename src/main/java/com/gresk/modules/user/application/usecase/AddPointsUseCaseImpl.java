package com.gresk.modules.user.application.usecase;

import com.gresk.modules.user.application.command.AddPointsCommand;
import com.gresk.modules.user.domain.exception.UserNotFoundException;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.in.AddPointsUseCase;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddPointsUseCaseImpl implements AddPointsUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public void execute(AddPointsCommand command) {
        UserId userId = UserId.from(command.userId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.addPoints(command.pointsToAdd());

        userRepository.save(user);
    }
}
