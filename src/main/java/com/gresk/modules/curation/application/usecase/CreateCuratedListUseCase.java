package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.application.command.CreateCuratedListCommand;
import com.gresk.modules.curation.application.port.in.CreateCuratedListPort;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.ListVisibility;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCuratedListUseCase implements CreateCuratedListPort {

    private final CuratedListRepository repository;

    @Override
    @Transactional
    public CuratedList execute(CreateCuratedListCommand command) {
        CuratedList list = CuratedList.create(
                UserId.from(command.ownerId()),
                command.title(),
                command.description(),
                ListVisibility.valueOf(command.visibility())
        );
        return repository.save(list);
    }
}
