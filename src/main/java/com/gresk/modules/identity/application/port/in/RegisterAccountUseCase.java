package com.gresk.modules.identity.application.port.in;

import com.gresk.modules.identity.application.command.RegisterAccountCommand;
import com.gresk.modules.identity.domain.model.AccountId;

public interface RegisterAccountUseCase {

    AccountId execute(RegisterAccountCommand command);
}
