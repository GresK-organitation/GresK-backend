package com.gresk.modules.account.application.port.in;

import com.gresk.modules.account.application.command.RegisterPromoterAccountCommand;
import com.gresk.modules.account.domain.model.AccountId;

public interface RegisterPromoterAccountUseCase {
    AccountId execute(RegisterPromoterAccountCommand command);

}
