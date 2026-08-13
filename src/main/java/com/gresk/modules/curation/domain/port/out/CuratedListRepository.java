package com.gresk.modules.curation.domain.port.out;

import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CuratedListRepository {
    CuratedList save(CuratedList list);
    Optional<CuratedList> findById(CuratedListId id);
    List<CuratedList> findAll(CuratedListFilter filter, PageRequest pageRequest);
    long count(CuratedListFilter filter);
    void deleteById(CuratedListId id);
}
