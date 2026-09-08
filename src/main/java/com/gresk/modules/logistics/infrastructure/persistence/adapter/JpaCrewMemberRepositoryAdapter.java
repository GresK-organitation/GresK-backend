package com.gresk.modules.logistics.infrastructure.persistence.adapter;

import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.model.CrewMemberId;
import com.gresk.modules.logistics.domain.port.out.CrewMemberRepositoryPort;
import com.gresk.modules.logistics.infrastructure.persistence.mapper.CrewMemberMapper;
import com.gresk.modules.logistics.infrastructure.persistence.repository.CrewMemberJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCrewMemberRepositoryAdapter implements CrewMemberRepositoryPort {

    private final CrewMemberJpaRepository jpaRepository;
    private final CrewMemberMapper mapper;

    @Override
    @Transactional
    public CrewMember save(CrewMember crewMember) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(crewMember)));
    }

    @Override
    public Optional<CrewMember> findById(CrewMemberId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<CrewMember> findByIdAndPromoterId(CrewMemberId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value()).map(mapper::toDomain);
    }

    @Override
    public List<CrewMember> findAllByPromoterId(PromoterId promoterId, boolean activeOnly) {
        var entities = activeOnly
                ? jpaRepository.findByPromoterIdAndActiveTrue(promoterId.value())
                : jpaRepository.findByPromoterId(promoterId.value());
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<CrewMember> findAllByIds(List<CrewMemberId> ids) {
        List<java.util.UUID> values = ids.stream().map(CrewMemberId::value).toList();
        return jpaRepository.findAllById(values).stream().map(mapper::toDomain).toList();
    }
}
