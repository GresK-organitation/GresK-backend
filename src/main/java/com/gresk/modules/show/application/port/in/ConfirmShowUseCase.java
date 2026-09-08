package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.domain.model.Show;

public interface ConfirmShowUseCase {
    Show execute(String showId, String promoterId);
}
