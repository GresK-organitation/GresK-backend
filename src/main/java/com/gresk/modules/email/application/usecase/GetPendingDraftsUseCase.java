package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.model.DraftReplyStatus;
import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.port.out.EmailDraftReplyRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Borradores pendientes de revisión del promotor. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPendingDraftsUseCase {

    private final EmailDraftReplyRepositoryPort repository;

    public List<EmailDraftReply> execute(UUID promoterId) {
        return repository.findByPromoterIdAndStatus(
                PromoterId.of(promoterId), DraftReplyStatus.PENDING_REVIEW);
    }
}
