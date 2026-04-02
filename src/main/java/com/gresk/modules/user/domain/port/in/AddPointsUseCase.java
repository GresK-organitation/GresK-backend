package com.gresk.modules.user.domain.port.in;

import com.gresk.modules.user.application.command.AddPointsCommand;

public interface AddPointsUseCase {
    void execute(AddPointsCommand command);
}
