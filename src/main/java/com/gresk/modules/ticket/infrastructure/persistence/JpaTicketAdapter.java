package com.gresk.modules.ticket.infrastructure.persistence;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaTicketAdapter implements TicketRepository {

    private final SpringDataTicketRepository repo;
    private final TicketMapper mapper;

    @Override
    public Ticket save(Ticket ticket) {
        return mapper.toDomain(repo.save(mapper.toEntity(ticket)));
    }

    @Override
    public Optional<Ticket> findById(TicketId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Ticket> findByUserId(UserId userId) {
        return repo.findByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndEventId(UserId userId, EventId eventId) {
        return repo.existsByUserIdAndEventId(userId.value(), eventId.value());
    }
}
