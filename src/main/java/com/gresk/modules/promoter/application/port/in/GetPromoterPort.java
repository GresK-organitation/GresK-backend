package com.gresk.modules.promoter.application.port.in;

import com.gresk.modules.promoter.application.query.GetPromoterQuery;
import com.gresk.modules.promoter.domain.model.Promoter;

public interface GetPromoterPort {
    Promoter execute(GetPromoterQuery query);
}
