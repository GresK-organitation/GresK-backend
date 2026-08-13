package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.exception.CuratedListForbiddenException;
import com.gresk.modules.curation.domain.exception.CuratedListNotFoundException;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.model.ListVisibility;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CuratedListAccessGuard {

    private final CuratedListRepository repository;

    /** Loads the list and requires the requester to be its owner. */
    CuratedList requireOwned(CuratedListId id, UserId requesterId) {
        CuratedList list = find(id);
        if (!list.getOwnerId().equals(requesterId)) {
            throw new CuratedListForbiddenException("This list does not belong to this user");
        }
        return list;
    }

    /** Loads the list and requires it to be visible to the requester (owner, or PUBLIC). */
    CuratedList requireVisible(CuratedListId id, UserId requesterId) {
        CuratedList list = find(id);
        boolean isOwner = list.getOwnerId().equals(requesterId);
        if (!isOwner && list.getVisibility() != ListVisibility.PUBLIC) {
            throw new CuratedListForbiddenException("This list is private");
        }
        return list;
    }

    private CuratedList find(CuratedListId id) {
        return repository.findById(id)
                .orElseThrow(() -> new CuratedListNotFoundException("Curated list not found: " + id));
    }
}
