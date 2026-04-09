package com.gresk.modules.account.application.port.in;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.account.application.command.LoginCommand;

public interface LoginUseCase {

    AuthToken execute(LoginCommand command);
}
