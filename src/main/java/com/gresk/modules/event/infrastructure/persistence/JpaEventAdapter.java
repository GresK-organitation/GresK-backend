package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaEventAdapter implements EventRepository {

    private final EventJpaRepository repo;
    private final EventMapper mapper;

    @Override
    public Event save(Event event) {
        return mapper.toDomain(repo.save(mapper.toEntity(event)));
    }

    @Override
    public Optional<Event> findById(EventId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(EventId id) {
        return repo.existsById(id.value());
    }

    @Override
    public <T> ScopedValue<T> findByIdWithLock(EventId eventId) {
        return null;
    }

    @Override
    public long count(EventFilter filter) {
        return repo.count(EventSpecifications.fromFilter(filter));
    }

    @Override
    public List<Event> findAll(EventFilter filter, PageRequest pageRequest) {
        return repo.findAll(EventSpecifications.fromFilter(filter), pageRequest)
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
