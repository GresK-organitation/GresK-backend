package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.command.UpdateShowDetailsCommand;
import com.gresk.modules.show.domain.model.Show;

public interface UpdateShowDetailsUseCase {
    Show execute(UpdateShowDetailsCommand command);
}
