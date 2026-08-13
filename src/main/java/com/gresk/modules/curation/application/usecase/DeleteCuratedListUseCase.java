package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.application.port.in.DeleteCuratedListPort;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCuratedListUseCase implements DeleteCuratedListPort {

    private final CuratedListRepository repository;
    private final CuratedListAccessGuard accessGuard;

    @Override
    @Transactional
    public void execute(String listId, String userId) {
        CuratedListId id = CuratedListId.of(listId);
        CuratedList list = accessGuard.requireOwned(id, UserId.from(userId));
        repository.deleteById(list.getId());
    }
}
