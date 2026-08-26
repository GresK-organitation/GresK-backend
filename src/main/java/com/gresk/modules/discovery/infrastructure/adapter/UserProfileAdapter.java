package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.discovery.domain.port.out.UserProfilePort;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.modules.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProfileAdapter implements UserProfilePort {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Optional<String> findCityByUserId(UserId userId) {
        return userRepositoryPort.findById(userId).map(user -> user.getCity().value());
    }
}
