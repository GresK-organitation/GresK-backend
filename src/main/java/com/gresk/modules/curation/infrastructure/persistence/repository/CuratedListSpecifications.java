package com.gresk.modules.curation.infrastructure.persistence.repository;

import com.gresk.modules.curation.domain.port.out.CuratedListFilter;
import com.gresk.modules.curation.infrastructure.persistence.entity.CuratedListEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CuratedListSpecifications {

    public static Specification<CuratedListEntity> fromFilter(CuratedListFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            filter.ownerId().ifPresent(ownerId ->
                    predicates.add(cb.equal(root.get("ownerId"), ownerId.value())));

            filter.visibility().ifPresent(visibility ->
                    predicates.add(cb.equal(root.get("visibility"), visibility)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
