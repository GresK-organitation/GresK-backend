package com.gresk.modules.artist.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.BandMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.BandMemberId;
import com.gresk.modules.artist.domain.port.out.BandMemberRepositoryPort;
import com.gresk.modules.artist.infrastructure.persistence.mapper.BandMemberMapper;
import com.gresk.modules.artist.infrastructure.persistence.repository.BandMemberJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaBandMemberRepositoryAdapter implements BandMemberRepositoryPort {

    private final BandMemberJpaRepository jpaRepository;
    private final BandMemberMapper        mapper;

    @Override
    @Transactional
    public BandMember save(BandMember member) {
        return jpaRepository.findById(member.getId().value())
                .map(entity -> {
                    entity.updateProfile(member.getName().value(), member.getRoleInBand(), member.isActive());
                    entity.replaceDocuments(mapper.toEmbeddableDocuments(member.getDocuments()));
                    return mapper.toDomain(jpaRepository.save(entity));
                })
                .orElseGet(() -> mapper.toDomain(jpaRepository.save(mapper.toEntity(member))));
    }

    @Override
    public Optional<BandMember> findById(BandMemberId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<BandMember> findByIdAndPromoterId(BandMemberId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value()).map(mapper::toDomain);
    }

    @Override
    public List<BandMember> findAllByArtistId(ArtistId artistId) {
        return jpaRepository.findByArtistId(artistId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<BandMember> findAllActiveWithDocuments() {
        return jpaRepository.findByActiveTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(BandMemberId id) {
        jpaRepository.deleteById(id.value());
    }
}
