package com.gresk.modules.email.infrastructure.persistence.adapter;

import com.gresk.modules.email.domain.model.DraftReplyStatus;
import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.model.EmailDraftReplyId;
import com.gresk.modules.email.domain.port.out.EmailDraftReplyRepositoryPort;
import com.gresk.modules.email.infrastructure.persistence.mapper.EmailDraftReplyMapper;
import com.gresk.modules.email.infrastructure.persistence.repository.EmailDraftReplyJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEmailDraftReplyRepositoryAdapter implements EmailDraftReplyRepositoryPort {

    private final EmailDraftReplyJpaRepository repo;
    private final EmailDraftReplyMapper        mapper;

    @Override
    @Transactional
    public EmailDraftReply save(EmailDraftReply draft) {
        return mapper.toDomain(repo.save(mapper.toEntity(draft)));
    }

    @Override
    public Optional<EmailDraftReply> findById(EmailDraftReplyId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<EmailDraftReply> findByPromoterIdAndStatus(PromoterId promoterId, DraftReplyStatus status) {
        return repo.findByPromoterIdAndStatus(promoterId.value(), status)
                .stream().map(mapper::toDomain).toList();
    }
}
