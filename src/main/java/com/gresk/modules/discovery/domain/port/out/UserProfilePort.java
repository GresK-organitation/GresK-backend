package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.user.domain.model.UserId;

import java.util.Optional;

/** Lookup de solo lectura al módulo `user`, para resolver la ciudad del usuario. */
public interface UserProfilePort {
    Optional<String> findCityByUserId(UserId userId);
}
