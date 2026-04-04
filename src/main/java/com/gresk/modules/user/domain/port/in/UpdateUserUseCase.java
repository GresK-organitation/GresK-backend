package com.gresk.modules.user.domain.port.in;

import com.gresk.modules.user.application.command.UpdateUserCommand;
import com.gresk.modules.user.domain.model.User;

import java.util.UUID;

public interface UpdateUserUseCase {
    User execute(UUID userId, UpdateUserCommand command);
}
