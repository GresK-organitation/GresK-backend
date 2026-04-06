package com.gresk.modules.user.infrastructure.persistence.repository;

import com.gresk.modules.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
}
