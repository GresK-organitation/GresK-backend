package com.gresk.modules.curation.infrastructure.persistence.adapter;

import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.port.out.CuratedListFilter;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.curation.infrastructure.persistence.mapper.CuratedListMapper;
import com.gresk.modules.curation.infrastructure.persistence.repository.CuratedListJpaRepository;
import com.gresk.modules.curation.infrastructure.persistence.repository.CuratedListSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCuratedListRepositoryAdapter implements CuratedListRepository {

    private final CuratedListJpaRepository repo;
    private final CuratedListMapper        mapper;

    @Override
    @Transactional
    public CuratedList save(CuratedList list) {
        return mapper.toDomain(repo.save(mapper.toEntity(list)));
    }

    @Override
    public Optional<CuratedList> findById(CuratedListId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<CuratedList> findAll(CuratedListFilter filter, PageRequest pageRequest) {
        return repo.findAll(CuratedListSpecifications.fromFilter(filter), pageRequest)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long count(CuratedListFilter filter) {
        return repo.count(CuratedListSpecifications.fromFilter(filter));
    }

    @Override
    @Transactional
    public void deleteById(CuratedListId id) {
        repo.deleteById(id.value());
    }
}
