package com.gresk.modules.email.infrastructure.persistence.adapter;

import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.model.EmailRiderVersionId;
import com.gresk.modules.email.domain.port.out.EmailRiderVersionRepositoryPort;
import com.gresk.modules.email.infrastructure.persistence.mapper.EmailRiderVersionMapper;
import com.gresk.modules.email.infrastructure.persistence.repository.EmailRiderVersionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEmailRiderVersionRepositoryAdapter implements EmailRiderVersionRepositoryPort {

    private final EmailRiderVersionJpaRepository repo;
    private final EmailRiderVersionMapper        mapper;

    @Override
    @Transactional
    public EmailRiderVersion save(EmailRiderVersion version) {
        return mapper.toDomain(repo.save(mapper.toEntity(version)));
    }

    @Override
    public Optional<EmailRiderVersion> findById(EmailRiderVersionId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<EmailRiderVersion> findByEventId(UUID eventId) {
        return repo.findByEventIdOrderByVersionNumberDesc(eventId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<EmailRiderVersion> findLatestByEventId(UUID eventId) {
        return repo.findFirstByEventIdOrderByVersionNumberDesc(eventId).map(mapper::toDomain);
    }
}
