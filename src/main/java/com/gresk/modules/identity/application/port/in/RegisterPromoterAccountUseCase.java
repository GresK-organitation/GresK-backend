package com.gresk.modules.identity.application.port.in;

import com.gresk.modules.identity.application.command.RegisterPromoterAccountCommand;
import com.gresk.modules.identity.domain.model.AccountId;

public interface RegisterPromoterAccountUseCase {
    AccountId execute(RegisterPromoterAccountCommand command);

}
