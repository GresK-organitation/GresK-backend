package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.PlaceHoldCommand;
import com.gresk.modules.show.domain.model.Show;

public interface PlaceHoldUseCase {
    Show execute(PlaceHoldCommand command);
}
