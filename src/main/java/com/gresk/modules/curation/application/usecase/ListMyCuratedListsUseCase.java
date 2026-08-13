package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.application.query.ListMyCuratedListsQuery;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.port.out.CuratedListFilter;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListMyCuratedListsUseCase {

    private final CuratedListRepository repository;

    @Transactional(readOnly = true)
    public List<CuratedList> execute(ListMyCuratedListsQuery query) {
        return repository.findAll(toFilter(query), PageRequest.of(query.page(), query.size()));
    }

    @Transactional(readOnly = true)
    public long count(ListMyCuratedListsQuery query) {
        return repository.count(toFilter(query));
    }

    private CuratedListFilter toFilter(ListMyCuratedListsQuery query) {
        return CuratedListFilter.ownedBy(UserId.from(query.userId()));
    }
}
