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

            // género (enum MusicGenre)
            filter.genre().ifPresent(g ->
                    predicates.add(cb.equal(root.get("genre"), g)));

            // ciudad (case-insensitive LIKE)
            filter.city().ifPresent(c ->
                    predicates.add(cb.like(cb.lower(root.get("city")),
                            "%" + c.toLowerCase() + "%")));

            // estado
            filter.status().ifPresent(s ->
                    predicates.add(cb.equal(root.get("status"), s)));

            // rango de fechas (Instant)
            filter.dateFrom().ifPresent(d ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), d)));

            filter.dateTo().ifPresent(d ->
                    predicates.add(cb.lessThan(root.get("eventDate"), d)));

            // rango de precio (sobre el precio original)
            filter.minPrice().ifPresent(min ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), min)));

            filter.maxPrice().ifPresent(max ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("amount"), max)));

            // artista (case-insensitive LIKE)
            filter.artistName().ifPresent(a ->
                    predicates.add(cb.like(cb.lower(root.get("artistName")),
                            "%" + a.toLowerCase() + "%")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
