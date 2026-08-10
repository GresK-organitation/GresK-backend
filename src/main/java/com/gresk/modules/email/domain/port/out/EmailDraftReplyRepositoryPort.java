package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.email.domain.model.DraftReplyStatus;
import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.model.EmailDraftReplyId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface EmailDraftReplyRepositoryPort {
    EmailDraftReply           save(EmailDraftReply draft);
    Optional<EmailDraftReply> findById(EmailDraftReplyId id);
    List<EmailDraftReply>     findByPromoterIdAndStatus(PromoterId promoterId, DraftReplyStatus status);
}
