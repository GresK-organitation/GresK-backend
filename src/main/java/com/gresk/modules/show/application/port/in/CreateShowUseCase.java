package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.CreateShowCommand;
import com.gresk.modules.show.domain.model.Show;

public interface CreateShowUseCase {
    Show execute(CreateShowCommand command);
}
