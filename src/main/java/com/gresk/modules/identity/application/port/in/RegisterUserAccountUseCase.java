package com.gresk.modules.identity.application.port.in;

import com.gresk.modules.identity.application.command.RegisterUserAccountCommand;
import com.gresk.modules.identity.domain.model.AccountId;

public interface RegisterUserAccountUseCase {

    AccountId execute(RegisterUserAccountCommand command);
}
