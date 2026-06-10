package com.gresk.modules.email.infrastructure.persistence.repository;

import com.gresk.modules.email.infrastructure.persistence.entity.EmailMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailMessageJpaRepository extends JpaRepository<EmailMessageEntity, UUID> {

    Optional<EmailMessageEntity> findByExternalMessageId(String externalMessageId);

    List<EmailMessageEntity> findByPromoterIdOrderByReceivedAtDesc(UUID promoterId);

    boolean existsByExternalMessageId(String externalMessageId);
}
