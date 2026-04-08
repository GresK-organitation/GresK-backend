package com.gresk.modules.user.infrastructure.persistence.mapper;

import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.infrastructure.persistence.entity.UserEntity;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;

import java.util.HashSet;

public class UserPersistenceMapper {

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId().value())
                .email(user.getEmail().value())
                .name(user.getName().value())
                .description(user.getDescription().value())
                .avatarAssetId(user.getAvatarAssetId().value())
                .city(user.getCity().value())
                .status(user.getStatus())
                .tier(user.getTier())
                .loyaltyPoints(user.getLoyaltyPoints())
                .musicGenres(new HashSet<>(user.getMusicGenres()))
                .roles(new HashSet<>(user.getRoles()))
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static User toDomain(UserEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getId()),
                Email.reconstitute(entity.getEmail()),
                Name.reconstitute(entity.getName()),
                Description.of(entity.getDescription()),
                City.of(entity.getCity()),
                new AssetId(entity.getAvatarAssetId()),
                new HashSet<>(entity.getMusicGenres()),
                entity.getStatus(),
                entity.getTier(),
                entity.getLoyaltyPoints(),
                new HashSet<>(entity.getRoles()),
                entity.getCreatedAt()
        );
    }
}
