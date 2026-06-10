package com.gresk.modules.email.infrastructure.persistence.adapter;

import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.email.infrastructure.persistence.mapper.EmailMessageMapper;
import com.gresk.modules.email.infrastructure.persistence.repository.EmailMessageJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEmailMessageRepositoryAdapter implements EmailMessageRepositoryPort {

    private final EmailMessageJpaRepository repo;
    private final EmailMessageMapper        mapper;

    @Override
    @Transactional
    public EmailMessage save(EmailMessage message) {
        return mapper.toDomain(repo.save(mapper.toEntity(message)));
    }

    @Override
    public Optional<EmailMessage> findById(EmailMessageId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<EmailMessage> findByExternalMessageId(String externalMessageId) {
        return repo.findByExternalMessageId(externalMessageId).map(mapper::toDomain);
    }

    @Override
    public List<EmailMessage> findByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterIdOrderByReceivedAtDesc(promoterId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByExternalMessageId(String externalMessageId) {
        return repo.existsByExternalMessageId(externalMessageId);
    }
}
