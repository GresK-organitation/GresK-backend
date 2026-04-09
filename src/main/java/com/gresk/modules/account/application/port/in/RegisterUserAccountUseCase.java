package com.gresk.modules.account.application.port.in;

import com.gresk.modules.account.application.command.RegisterUserAccountCommand;
import com.gresk.modules.account.domain.model.AccountId;

public interface RegisterUserAccountUseCase {

    AccountId execute(RegisterUserAccountCommand command);
}
