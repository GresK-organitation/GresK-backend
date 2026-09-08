package com.gresk.modules.agenda.application.port.in;

import com.gresk.modules.agenda.application.command.DisconnectCalendarCommand;

public interface DisconnectCalendarUseCase {
    void execute(DisconnectCalendarCommand command);
}
