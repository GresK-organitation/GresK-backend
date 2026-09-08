package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.CreateCrewMemberCommand;
import com.gresk.modules.logistics.domain.model.CrewMember;

public interface CreateCrewMemberUseCase {
    CrewMember execute(CreateCrewMemberCommand command);
}
