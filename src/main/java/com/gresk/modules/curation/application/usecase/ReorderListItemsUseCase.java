package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.model.CuratedListItemId;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReorderListItemsUseCase {

    private final CuratedListRepository  repository;
    private final CuratedListAccessGuard accessGuard;

    @Transactional
    public CuratedList execute(String listId, String userId, List<String> orderedItemIds) {
        CuratedList list = accessGuard.requireOwned(CuratedListId.of(listId), UserId.from(userId));
        list.reorder(orderedItemIds.stream().map(CuratedListItemId::of).toList());
        return repository.save(list);
    }
}
