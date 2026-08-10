package com.gresk.modules.email.infrastructure.persistence.repository;

import com.gresk.modules.email.domain.model.DraftReplyStatus;
import com.gresk.modules.email.infrastructure.persistence.entity.EmailDraftReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailDraftReplyJpaRepository extends JpaRepository<EmailDraftReplyEntity, UUID> {

    List<EmailDraftReplyEntity> findByPromoterIdAndStatus(UUID promoterId, DraftReplyStatus status);
}
