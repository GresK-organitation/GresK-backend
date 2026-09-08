package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.AddLogEntryCommand;
import com.gresk.modules.show.domain.model.ShowLogEntry;

public interface AddLogEntryUseCase {
    ShowLogEntry execute(AddLogEntryCommand command);
}
