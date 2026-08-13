package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.application.command.AddListItemCommand;
import com.gresk.modules.curation.application.port.in.AddListItemPort;
import com.gresk.modules.curation.domain.exception.ReferencedEntryNotFoundException;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.model.ListedEntryType;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.curation.domain.port.out.JournalEntryLookupPort;
import com.gresk.modules.curation.domain.port.out.ReviewLookupPort;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddListItemUseCase implements AddListItemPort {

    private final CuratedListRepository  repository;
    private final CuratedListAccessGuard accessGuard;
    private final ReviewLookupPort       reviewLookupPort;
    private final JournalEntryLookupPort journalEntryLookupPort;

    @Override
    @Transactional
    public CuratedList execute(AddListItemCommand command) {
        CuratedList list = accessGuard.requireOwned(
                CuratedListId.of(command.listId()), UserId.from(command.userId()));

        ListedEntryType entryType = ListedEntryType.valueOf(command.entryType());
        UUID entryId = UUID.fromString(command.entryId());

        boolean exists = (entryType == ListedEntryType.VERIFIED_REVIEW)
                ? reviewLookupPort.existsById(entryId)
                : journalEntryLookupPort.existsById(entryId);
        if (!exists) {
            throw new ReferencedEntryNotFoundException(entryType + " not found: " + entryId);
        }

        list.addItem(entryType, entryId);
        return repository.save(list);
    }
}
