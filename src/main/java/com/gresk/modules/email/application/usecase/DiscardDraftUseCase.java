package com.gresk.modules.email.application.usecase;

import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.port.out.EmailDraftReplyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Descarta un borrador pendiente: no se enviará. */
@Service
@RequiredArgsConstructor
@Transactional
public class DiscardDraftUseCase {

    private final EmailDraftReplyRepositoryPort repository;

    public void execute(UUID draftId, UUID promoterId) {
        EmailDraftReply draft = GetDraftDetailUseCase.loadOwnedDraft(draftId, promoterId, repository);
        draft.discard();
        repository.save(draft);
    }
}
