package com.gresk.modules.promoter.application.port.in;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.promoter.application.command.AuthenticatePromoterCommand;

public interface AuthenticatePromoterPort {
    AuthToken execute(AuthenticatePromoterCommand command);
}
