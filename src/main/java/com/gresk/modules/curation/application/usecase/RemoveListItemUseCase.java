package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.model.CuratedListItemId;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveListItemUseCase {

    private final CuratedListRepository  repository;
    private final CuratedListAccessGuard accessGuard;

    @Transactional
    public CuratedList execute(String listId, String userId, String itemId) {
        CuratedList list = accessGuard.requireOwned(CuratedListId.of(listId), UserId.from(userId));
        list.removeItem(CuratedListItemId.of(itemId));
        return repository.save(list);
    }
}
