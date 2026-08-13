package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.application.query.DiscoverCuratedListsQuery;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.port.out.CuratedListFilter;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscoverPublicCuratedListsUseCase {

    private final CuratedListRepository repository;

    @Transactional(readOnly = true)
    public List<CuratedList> execute(DiscoverCuratedListsQuery query) {
        return repository.findAll(CuratedListFilter.discoverPublic(), PageRequest.of(query.page(), query.size()));
    }

    @Transactional(readOnly = true)
    public long count(DiscoverCuratedListsQuery query) {
        return repository.count(CuratedListFilter.discoverPublic());
    }
}
