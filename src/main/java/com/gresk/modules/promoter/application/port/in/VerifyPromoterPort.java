package com.gresk.modules.promoter.application.port.in;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

public interface VerifyPromoterPort {
    void execute(PromoterId id);
}
