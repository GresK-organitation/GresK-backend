package com.gresk.modules.email.infrastructure.persistence.adapter;

import com.gresk.modules.email.domain.model.EmailEntityRecord;
import com.gresk.modules.email.domain.model.EmailEntityRecordId;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.domain.port.out.EmailEntityRecordRepositoryPort;
import com.gresk.modules.email.infrastructure.persistence.mapper.EmailEntityRecordMapper;
import com.gresk.modules.email.infrastructure.persistence.repository.EmailEntityRecordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEmailEntityRecordRepositoryAdapter implements EmailEntityRecordRepositoryPort {

    private final EmailEntityRecordJpaRepository repo;
    private final EmailEntityRecordMapper        mapper;

    @Override
    @Transactional
    public EmailEntityRecord save(EmailEntityRecord record) {
        return mapper.toDomain(repo.save(mapper.toEntity(record)));
    }

    @Override
    @Transactional
    public List<EmailEntityRecord> saveAll(List<EmailEntityRecord> records) {
        return repo.saveAll(records.stream().map(mapper::toEntity).toList())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<EmailEntityRecord> findById(EmailEntityRecordId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<EmailEntityRecord> findByEmailId(EmailMessageId emailId) {
        return repo.findByEmailId(emailId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<EmailEntityRecord> findPendingAction() {
        return repo.findByRequiresActionTrueAndActionedAtIsNull()
                .stream().map(mapper::toDomain).toList();
    }
}
