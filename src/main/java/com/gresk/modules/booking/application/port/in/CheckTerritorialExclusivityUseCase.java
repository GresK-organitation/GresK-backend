package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.query.CheckTerritorialExclusivityQuery;
import com.gresk.modules.booking.application.query.TerritorialConflict;

import java.util.List;

public interface CheckTerritorialExclusivityUseCase {
    List<TerritorialConflict> execute(CheckTerritorialExclusivityQuery query);
}
