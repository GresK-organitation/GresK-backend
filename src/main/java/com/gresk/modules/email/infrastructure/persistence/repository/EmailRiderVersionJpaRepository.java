package com.gresk.modules.email.infrastructure.persistence.repository;

import com.gresk.modules.email.infrastructure.persistence.entity.EmailRiderVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailRiderVersionJpaRepository extends JpaRepository<EmailRiderVersionEntity, UUID> {

    List<EmailRiderVersionEntity> findByEventIdOrderByVersionNumberDesc(UUID eventId);

    Optional<EmailRiderVersionEntity> findFirstByEventIdOrderByVersionNumberDesc(UUID eventId);
}
