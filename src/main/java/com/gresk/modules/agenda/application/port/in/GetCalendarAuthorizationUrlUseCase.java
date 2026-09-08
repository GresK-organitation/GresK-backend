package com.gresk.modules.agenda.application.port.in;

import com.gresk.modules.agenda.application.query.GetCalendarAuthorizationUrlQuery;

public interface GetCalendarAuthorizationUrlUseCase {
    String execute(GetCalendarAuthorizationUrlQuery query);
}
