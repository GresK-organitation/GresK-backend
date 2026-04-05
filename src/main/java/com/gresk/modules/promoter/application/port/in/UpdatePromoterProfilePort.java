package com.gresk.modules.promoter.application.port.in;

import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;

public interface UpdatePromoterProfilePort {
    void execute(UpdatePromoterProfileCommand command);
}
