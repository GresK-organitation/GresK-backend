package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.query.GetDaySheetQuery;
import com.gresk.modules.booking.domain.model.valueobject.DaySheet;

public interface GetDaySheetUseCase {
    DaySheet execute(GetDaySheetQuery query);
}
