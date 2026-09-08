package com.gresk.modules.agenda.application.port.in;

import com.gresk.modules.agenda.application.command.CompleteCalendarConnectionCommand;
import com.gresk.modules.agenda.domain.model.CalendarSyncAccount;

public interface CompleteCalendarConnectionUseCase {
    CalendarSyncAccount execute(CompleteCalendarConnectionCommand command);
}
