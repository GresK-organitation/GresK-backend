package com.gresk.modules.agenda.application.port.in;

import com.gresk.modules.agenda.application.query.ListCalendarAccountsQuery;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;

import java.util.List;

public interface ListCalendarAccountsUseCase {
    List<CalendarSyncAccount> execute(ListCalendarAccountsQuery query);
}
