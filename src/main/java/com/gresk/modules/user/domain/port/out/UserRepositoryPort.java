package com.gresk.modules.user.domain.port.out;

import com.gresk.modules.user.domain.model.User;
import com.gresk.modules.user.domain.model.UserId;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(UserId id);

    User save(User user);
}
