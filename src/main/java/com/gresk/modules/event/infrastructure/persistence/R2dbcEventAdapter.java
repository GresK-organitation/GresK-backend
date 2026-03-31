package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class R2dbcEventAdapter implements EventRepository {

    private final EventR2dbcRepository repo;
    private final DatabaseClient client;

    @Override
    public Mono<Event> save(Event event) {
        UUID id = event.getId().value();
        EventEntity entity = EventMapper.toEntity(event);
        return repo.existsById(id)
                .flatMap(exists -> {
                    entity.setNew(!exists);
                    return repo.save(entity);
                })
                .map(EventMapper::toDomain);
    }

    @Override
    public Mono<Event> findById(EventId id) {
        return repo.findById(id.value())
                .map(EventMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(EventId id) {
        return repo.existsById(id.value());
    }

    @Override
    public Mono<Long> count(EventFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM events WHERE 1=1");
        Map<String, Object> params = buildFilterParams(filter, sql);

        DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }
        return spec.map((row, meta) -> row.get(0, Long.class)).one();
    }

    @Override
    public Flux<Event> findAll(EventFilter filter, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("SELECT * FROM events WHERE 1=1");
        Map<String, Object> params = buildFilterParams(filter, sql);

        sql.append(" ORDER BY event_date ASC NULLS LAST LIMIT :limit OFFSET :offset");
        params.put("limit", pageRequest.getPageSize());
        params.put("offset", pageRequest.getOffset());

        DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }
        return spec.map((row, meta) -> EventMapper.fromRow(row)).all();
    }

    // --- helpers ---

    private Map<String, Object> buildFilterParams(EventFilter filter, StringBuilder sql) {
        Map<String, Object> params = new LinkedHashMap<>();

        filter.status().ifPresent(s -> {
            sql.append(" AND status = :status");
            params.put("status", s.name());
        });
        filter.genre().ifPresent(g -> {
            sql.append(" AND genre = :genre");
            params.put("genre", g.name());
        });
        filter.city().ifPresent(c -> {
            sql.append(" AND city = :city");
            params.put("city", c);
        });
        filter.dateFrom().ifPresent(d -> {
            sql.append(" AND event_date >= :dateFrom");
            params.put("dateFrom", d.atStartOfDay().atOffset(ZoneOffset.UTC));
        });
        filter.dateTo().ifPresent(d -> {
            sql.append(" AND event_date < :dateTo");
            params.put("dateTo", d.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC));
        });

        return params;
    }
}
