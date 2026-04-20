package com.gresk.modules.review.infrastructure.adapter;

import com.gresk.modules.review.domain.port.out.UserPointsPort;
import com.gresk.modules.user.application.command.AddPointsCommand;
import com.gresk.modules.user.domain.port.in.AddPointsUseCase;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPointsAdapter implements UserPointsPort {

    private final AddPointsUseCase addPointsUseCase;

    @Override
    public void addPoints(UserId userId, int points) {
        addPointsUseCase.execute(new AddPointsCommand(userId.value().toString(), points));
    }
}
