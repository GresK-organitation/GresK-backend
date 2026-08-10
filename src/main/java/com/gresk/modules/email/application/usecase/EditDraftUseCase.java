package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.port.out.EmailDraftReplyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Edición del cuerpo de un borrador antes de aprobarlo. */
@Service
@RequiredArgsConstructor
@Transactional
public class EditDraftUseCase {

    private final EmailDraftReplyRepositoryPort repository;

    public EmailDraftReply execute(UUID draftId, UUID promoterId, String newBody) {
        EmailDraftReply draft = GetDraftDetailUseCase.loadOwnedDraft(draftId, promoterId, repository);
        draft.edit(newBody);
        return repository.save(draft);
    }
}
