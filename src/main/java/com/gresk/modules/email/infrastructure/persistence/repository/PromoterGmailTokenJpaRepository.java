package com.gresk.modules.email.infrastructure.persistence.repository;

import com.gresk.modules.email.infrastructure.persistence.entity.PromoterGmailTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PromoterGmailTokenJpaRepository extends JpaRepository<PromoterGmailTokenEntity, UUID> {

    Optional<PromoterGmailTokenEntity> findByGmailAddress(String gmailAddress);
}
