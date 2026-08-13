package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCuratedListUseCase {

    private final CuratedListAccessGuard accessGuard;

    @Transactional(readOnly = true)
    public CuratedList execute(String listId, String requesterUserId) {
        return accessGuard.requireVisible(CuratedListId.of(listId), UserId.from(requesterUserId));
    }
}
