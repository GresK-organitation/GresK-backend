package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.port.out.EventFilter;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class EventSpecifications {

    public static Specification<EventEntity> fromFilter(EventFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            filter.genre().ifPresent(g ->
                    predicates.add(cb.equal(root.get("genre"), g)));

            filter.city().ifPresent(c ->
                    predicates.add(cb.like(cb.lower(root.get("city")),
                            "%" + c.toLowerCase() + "%")));

            filter.status().ifPresent(s ->
                    predicates.add(cb.equal(root.get("status"), s)));

            filter.dateFrom().ifPresent(d ->
                    predicates.add(cb.greaterThanOrEqualTo(
                            root.get("eventDate"), d.atStartOfDay())));

            filter.dateTo().ifPresent(d ->
                    predicates.add(cb.lessThan(
                            root.get("eventDate"), d.plusDays(1).atStartOfDay())));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
