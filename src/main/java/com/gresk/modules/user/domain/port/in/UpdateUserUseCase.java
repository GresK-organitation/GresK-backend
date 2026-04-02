package com.gresk.modules.user.domain.port.in;

import com.gresk.modules.user.application.command.UpdateUserCommand;
import com.gresk.modules.user.domain.model.User;

public interface UpdateUserUseCase {
    User execute(UpdateUserCommand command);
}
