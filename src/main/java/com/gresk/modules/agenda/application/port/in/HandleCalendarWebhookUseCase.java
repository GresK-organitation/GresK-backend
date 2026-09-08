package com.gresk.modules.agenda.application.port.in;

import com.gresk.modules.agenda.application.command.HandleCalendarWebhookCommand;

public interface HandleCalendarWebhookUseCase {
    void execute(HandleCalendarWebhookCommand command);
}
