package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.SettleShowCommand;
import com.gresk.modules.show.domain.model.Show;

public interface SettleShowUseCase {
    Show execute(SettleShowCommand command);
}
