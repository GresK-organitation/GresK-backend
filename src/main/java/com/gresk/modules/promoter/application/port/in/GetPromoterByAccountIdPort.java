package com.gresk.modules.promoter.application.port.in;

import com.gresk.modules.promoter.domain.model.Promoter;

import java.util.UUID;

public interface GetPromoterByAccountIdPort {

    Promoter execute(UUID accountId);
}
