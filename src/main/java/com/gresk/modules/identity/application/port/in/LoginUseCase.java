package com.gresk.modules.identity.application.port.in;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.identity.application.command.LoginCommand;

public interface LoginUseCase {

    AuthToken execute(LoginCommand command);
}
