package com.gresk.modules.email.infrastructure.persistence.adapter;

import com.gresk.modules.email.domain.model.PromoterGmailToken;
import com.gresk.modules.email.domain.port.out.PromoterGmailTokenRepositoryPort;
import com.gresk.modules.email.infrastructure.persistence.mapper.PromoterGmailTokenMapper;
import com.gresk.modules.email.infrastructure.persistence.repository.PromoterGmailTokenJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPromoterGmailTokenRepositoryAdapter implements PromoterGmailTokenRepositoryPort {

    private final PromoterGmailTokenJpaRepository repo;
    private final PromoterGmailTokenMapper        mapper;

    @Override
    @Transactional
    public PromoterGmailToken save(PromoterGmailToken token) {
        return mapper.toDomain(repo.save(mapper.toEntity(token)));
    }

    @Override
    public Optional<PromoterGmailToken> findByPromoterId(PromoterId promoterId) {
        return repo.findById(promoterId.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteByPromoterId(PromoterId promoterId) {
        repo.deleteById(promoterId.value());
    }
}
