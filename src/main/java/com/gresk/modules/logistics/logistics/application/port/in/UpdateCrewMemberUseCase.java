package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.command.UpdateCrewMemberCommand;
import com.gresk.modules.logistics.domain.model.CrewMember;

public interface UpdateCrewMemberUseCase {
    CrewMember execute(UpdateCrewMemberCommand command);
}
