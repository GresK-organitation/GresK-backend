package com.gresk.modules.artist.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.RosterMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.RosterMemberId;
import com.gresk.modules.artist.domain.port.out.RosterMemberRepositoryPort;
import com.gresk.modules.artist.infrastructure.persistence.mapper.RosterMemberMapper;
import com.gresk.modules.artist.infrastructure.persistence.repository.RosterMemberJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaRosterMemberRepositoryAdapter implements RosterMemberRepositoryPort {

    private final RosterMemberJpaRepository jpaRepository;
    private final RosterMemberMapper        mapper;

    @Override
    @Transactional
    public RosterMember save(RosterMember member) {
        return jpaRepository.findById(member.getId().value())
                .map(entity -> {
                    entity.updateContactInfo(member.getName().value(), member.getRole(),
                            member.getPhone(), member.getEmail() != null ? member.getEmail().value() : null);
                    var billing = member.getBillingDetails();
                    entity.updateBilling(billing != null ? billing.legalName() : null,
                            billing != null ? billing.taxId() : null,
                            billing != null ? billing.billingAddress() : null,
                            billing != null ? billing.iban() : null);
                    entity.updateFlags(member.isPrimary(), member.isActive());
                    return mapper.toDomain(jpaRepository.save(entity));
                })
                .orElseGet(() -> mapper.toDomain(jpaRepository.save(mapper.toEntity(member))));
    }

    @Override
    public Optional<RosterMember> findById(RosterMemberId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<RosterMember> findByIdAndPromoterId(RosterMemberId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value()).map(mapper::toDomain);
    }

    @Override
    public List<RosterMember> findAllByArtistId(ArtistId artistId) {
        return jpaRepository.findByArtistId(artistId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(RosterMemberId id) {
        jpaRepository.deleteById(id.value());
    }
}
