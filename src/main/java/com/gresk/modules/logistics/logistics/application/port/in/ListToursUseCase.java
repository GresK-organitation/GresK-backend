package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.query.ListToursQuery;
import com.gresk.modules.logistics.domain.model.Tour;

import java.util.List;

public interface ListToursUseCase {
    List<Tour> execute(ListToursQuery query);
}
