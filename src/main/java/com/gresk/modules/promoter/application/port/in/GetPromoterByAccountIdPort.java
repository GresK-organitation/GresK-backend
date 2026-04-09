package com.gresk.modules.promoter.application.port.in;

import com.gresk.modules.promoter.application.dto.PromoterProfileDTO;

import java.util.UUID;

public interface GetPromoterByAccountIdPort {

    PromoterProfileDTO execute(UUID accountId);
}
