package com.gresk.modules.user.domain.port.in;

import com.gresk.modules.user.application.dto.UserDashboardDTO;
import com.gresk.modules.user.domain.model.UserId;

import java.util.UUID;

public interface GetUserDashboardUseCase {
    UserDashboardDTO execute(UUID userId);
}
