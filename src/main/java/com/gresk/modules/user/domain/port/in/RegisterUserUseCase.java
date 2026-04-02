package com.gresk.modules.user.domain.port.in;

import com.gresk.modules.user.application.command.RegisterUserCommand;
import com.gresk.modules.user.domain.model.UserId;

public interface RegisterUserUseCase {
    UserId execute(RegisterUserCommand command);
}
