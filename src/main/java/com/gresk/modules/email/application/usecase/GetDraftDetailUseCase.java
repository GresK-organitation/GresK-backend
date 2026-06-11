package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.application.dto.DraftDetail;
import com.gresk.modules.email.domain.exception.DraftReplyNotFoundException;
import com.gresk.modules.email.domain.exception.EmailMessageNotFoundException;
import com.gresk.modules.email.domain.exception.ForbiddenEmailOperationException;
import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.model.EmailDraftReplyId;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailDraftReplyRepositoryPort;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Detalle de un borrador junto al email original al que responde. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDraftDetailUseCase {

    private final EmailDraftReplyRepositoryPort draftRepository;
    private final EmailMessageRepositoryPort    emailRepository;

    public DraftDetail execute(UUID draftId, UUID promoterId) {
        EmailDraftReply draft = loadOwnedDraft(draftId, promoterId, draftRepository);

        EmailMessage original = emailRepository.findById(draft.getEmailId())
                .orElseThrow(() -> new EmailMessageNotFoundException(draft.getEmailId()));

        return new DraftDetail(draft, original);
    }

    /** Carga + verificación de propiedad, compartida por los use cases de borradores. */
    static EmailDraftReply loadOwnedDraft(UUID draftId, UUID promoterId,
                                          EmailDraftReplyRepositoryPort repository) {
        EmailDraftReplyId id = EmailDraftReplyId.of(draftId);
        EmailDraftReply draft = repository.findById(id)
                .orElseThrow(() -> new DraftReplyNotFoundException(id));

        if (!draft.isOwnedBy(PromoterId.of(promoterId))) {
            throw new ForbiddenEmailOperationException(
                    "Draft " + draftId + " does not belong to promoter " + promoterId);
        }
        return draft;
    }
}
