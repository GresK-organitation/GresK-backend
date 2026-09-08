package com.gresk.modules.logistics.domain.port.out;

import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.model.CrewMemberId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface CrewMemberRepositoryPort {
    CrewMember save(CrewMember crewMember);
    Optional<CrewMember> findById(CrewMemberId id);
    Optional<CrewMember> findByIdAndPromoterId(CrewMemberId id, PromoterId promoterId);
    List<CrewMember> findAllByPromoterId(PromoterId promoterId, boolean activeOnly);
    List<CrewMember> findAllByIds(List<CrewMemberId> ids);
}
