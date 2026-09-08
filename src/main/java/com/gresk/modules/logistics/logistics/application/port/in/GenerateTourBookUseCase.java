package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.dto.TourBookResponse;
import com.gresk.modules.logistics.application.query.GenerateTourBookQuery;

public interface GenerateTourBookUseCase {
    TourBookResponse execute(GenerateTourBookQuery query);
}
