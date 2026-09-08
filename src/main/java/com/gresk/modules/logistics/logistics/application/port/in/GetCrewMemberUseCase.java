package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.query.GetCrewMemberQuery;
import com.gresk.modules.logistics.domain.model.CrewMember;

public interface GetCrewMemberUseCase {
    CrewMember execute(GetCrewMemberQuery query);
}
