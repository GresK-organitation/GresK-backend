package com.gresk.modules.user.infrastructure.in.rest.mapper;

import com.gresk.modules.user.application.command.RegisterUserCommand;
import com.gresk.modules.user.application.command.UpdateUserCommand;
import com.gresk.modules.user.infrastructure.in.rest.dto.request.RegisterUserRequest;
import com.gresk.modules.user.infrastructure.in.rest.dto.request.UpdateUserProfileRequest;

public class UserRestMapper {
    public static RegisterUserCommand toRegisterUserCommand(RegisterUserRequest request) {
        return new RegisterUserCommand(
                request.email(),
                request.password(),
                request.name(),
                request.description(),
                request.city(),
                request.musicGenres()
        );
    }

    public static UpdateUserCommand toUpdateUsercommand(UpdateUserProfileRequest request) {
        return new UpdateUserCommand(request.name(), request.description(), request.city(), request.musicGenres());
    }
}
