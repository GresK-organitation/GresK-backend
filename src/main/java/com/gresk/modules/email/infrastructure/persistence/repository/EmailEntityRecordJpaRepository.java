package com.gresk.modules.email.infrastructure.persistence.repository;

import com.gresk.modules.email.infrastructure.persistence.entity.EmailEntityRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EmailEntityRecordJpaRepository extends JpaRepository<EmailEntityRecordEntity, UUID> {

    List<EmailEntityRecordEntity> findByEmailId(UUID emailId);

    List<EmailEntityRecordEntity> findByEmailIdIn(Collection<UUID> emailIds);

    List<EmailEntityRecordEntity> findByRequiresActionTrueAndActionedAtIsNull();
}
