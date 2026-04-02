package com.gresk.modules.user.domain.port.in;

import com.gresk.modules.user.application.dto.UserDashboardDTO;
import com.gresk.modules.user.domain.model.UserId;

public interface GetUserDashboardUseCase {
    UserDashboardDTO execute(UserId userId);
}
