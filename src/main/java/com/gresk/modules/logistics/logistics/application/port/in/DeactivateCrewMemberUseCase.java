package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.DeactivateCrewMemberCommand;
import com.gresk.modules.logistics.domain.model.CrewMember;

public interface DeactivateCrewMemberUseCase {
    CrewMember execute(DeactivateCrewMemberCommand command);
}
