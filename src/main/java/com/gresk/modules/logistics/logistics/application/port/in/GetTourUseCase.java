package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.query.GetTourQuery;
import com.gresk.modules.logistics.domain.model.Tour;

public interface GetTourUseCase {
    Tour execute(GetTourQuery query);
}
