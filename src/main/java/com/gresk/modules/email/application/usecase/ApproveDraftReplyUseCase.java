package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.application.command.ApproveDraftReplyCommand;
import com.gresk.modules.email.domain.exception.DraftReplyNotFoundException;
import com.gresk.modules.email.domain.exception.EmailMessageNotFoundException;
import com.gresk.modules.email.domain.exception.ForbiddenEmailOperationException;
import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.model.EmailDraftReplyId;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.port.out.EmailDraftReplyRepositoryPort;
import com.gresk.modules.email.domain.port.out.EmailMessageRepositoryPort;
import com.gresk.modules.email.domain.port.out.EmailSenderPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * La promotora aprueba un borrador (opcionalmente editado) y se envía como
 * respuesta al remitente del correo original. Si el envío falla, la
 * transacción revierte y el borrador sigue en PENDING_REVIEW.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ApproveDraftReplyUseCase {

    private final EmailDraftReplyRepositoryPort draftRepository;
    private final EmailMessageRepositoryPort    emailRepository;
    private final EmailSenderPort               emailSender;

    public EmailDraftReply execute(ApproveDraftReplyCommand cmd) {
        EmailDraftReplyId draftId = EmailDraftReplyId.of(cmd.draftId());
        EmailDraftReply draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new DraftReplyNotFoundException(draftId));

        if (!draft.isOwnedBy(PromoterId.of(cmd.promoterId()))) {
            throw new ForbiddenEmailOperationException(
                    "Draft " + draftId + " does not belong to promoter " + cmd.promoterId());
        }

        EmailMessage original = emailRepository.findById(draft.getEmailId())
                .orElseThrow(() -> new EmailMessageNotFoundException(draft.getEmailId()));

        draft.approve(cmd.editedBody());
        emailSender.send(original.getFromAddress(), draft.getSubject(), draft.effectiveBody());
        draft.markSent();

        return draftRepository.save(draft);
    }
}
