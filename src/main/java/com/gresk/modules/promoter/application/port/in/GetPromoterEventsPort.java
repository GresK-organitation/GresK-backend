package com.gresk.modules.promoter.application.port.in;

import com.gresk.modules.promoter.application.dto.PromoterEventDTO;

import java.util.List;
import java.util.UUID;

public interface GetPromoterEventsPort {
    List<PromoterEventDTO> execute(UUID accountId);
}
