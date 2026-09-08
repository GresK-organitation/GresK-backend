package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.CancelShowCommand;
import com.gresk.modules.show.domain.model.Show;

public interface CancelShowUseCase {
    Show execute(CancelShowCommand command);
}
