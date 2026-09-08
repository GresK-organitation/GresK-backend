package com.gresk.modules.logistics.application.port.in;

import com.gresk.modules.logistics.application.query.ListCrewMembersQuery;
import com.gresk.modules.logistics.domain.model.CrewMember;

import java.util.List;

public interface ListCrewMembersUseCase {
    List<CrewMember> execute(ListCrewMembersQuery query);
}
