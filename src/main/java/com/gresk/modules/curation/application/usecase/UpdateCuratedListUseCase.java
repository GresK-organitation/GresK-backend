package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.application.command.UpdateCuratedListCommand;
import com.gresk.modules.curation.application.port.in.UpdateCuratedListPort;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.model.ListVisibility;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCuratedListUseCase implements UpdateCuratedListPort {

    private final CuratedListRepository repository;
    private final CuratedListAccessGuard accessGuard;

    @Override
    @Transactional
    public CuratedList execute(UpdateCuratedListCommand command) {
        CuratedList list = accessGuard.requireOwned(
                CuratedListId.of(command.listId()), UserId.from(command.userId()));

        list.rename(command.title(), command.description());
        list.changeVisibility(ListVisibility.valueOf(command.visibility()));

        return repository.save(list);
    }
}
