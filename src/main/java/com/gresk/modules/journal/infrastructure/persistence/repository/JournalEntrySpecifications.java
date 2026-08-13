package com.gresk.modules.journal.infrastructure.persistence.repository;

import com.gresk.modules.journal.domain.port.out.JournalEntryFilter;
import com.gresk.modules.journal.infrastructure.persistence.entity.JournalEntryEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class JournalEntrySpecifications {

    public static Specification<JournalEntryEntity> fromFilter(JournalEntryFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("userId"), filter.userId().value()));

            filter.artistId().ifPresent(artistId ->
                    predicates.add(cb.equal(root.get("artistId"), artistId.value())));

            filter.genre().ifPresent(genre ->
                    predicates.add(cb.equal(root.get("genre"), genre)));

            filter.dateFrom().ifPresent(from ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("approxDate"), from)));

            filter.dateTo().ifPresent(to ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("approxDate"), to)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
