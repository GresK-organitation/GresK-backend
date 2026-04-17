package com.gresk.modules.user.infrastructure.persistence;

import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import com.gresk.modules.user.infrastructure.persistence.entity.UserEntity;
import com.gresk.modules.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.gresk.modules.user.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = jpaRepository.findById(user.getId().value())
                .map(existing -> {
                    existing.updateProfile(
                            user.getName().value(),
                            user.getDescription().value(),
                            user.getCity().value(),
                            new HashSet<>(user.getMusicGenres())
                    );
                    existing.updateAvatar(user.getAvatarAssetId().value());
                    existing.updateTier(user.getTier());
                    existing.updateLoyaltyPoints(user.getLoyaltyPoints());
                    return existing;
                })
                .orElseGet(() -> mapper.toEntity(user));

        UserEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
