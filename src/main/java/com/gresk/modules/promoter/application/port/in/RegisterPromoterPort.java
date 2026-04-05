package com.gresk.modules.promoter.application.port.in;

import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;

public interface RegisterPromoterPort {
    PromoterId execute(RegisterPromoterCommand command);
}
