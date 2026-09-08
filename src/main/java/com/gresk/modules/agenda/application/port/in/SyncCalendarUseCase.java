package com.gresk.modules.agenda.application.port.in;

import com.gresk.modules.agenda.application.command.SyncCalendarCommand;
import com.gresk.modules.agenda.application.dto.SyncResult;

public interface SyncCalendarUseCase {
    SyncResult execute(SyncCalendarCommand command);
}
